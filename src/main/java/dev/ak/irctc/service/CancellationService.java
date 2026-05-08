package dev.ak.irctc.service;

import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Passenger;
import dev.ak.irctc.entity.SeatInventory;
import dev.ak.irctc.enums.BookingStatus;
import dev.ak.irctc.enums.PassengerStatus;
import dev.ak.irctc.enums.SeatStatus;
import dev.ak.irctc.repository.BookingRepository;
import dev.ak.irctc.repository.PassengerRepository;
import dev.ak.irctc.repository.SeatInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CancellationService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatInventoryRepository seatInventoryRepository;

    public CancellationService(BookingRepository bookingRepository, PassengerRepository passengerRepository, SeatInventoryRepository seatInventoryRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.seatInventoryRepository = seatInventoryRepository;
    }

    @Transactional
    public void cancelPassengers(UUID bookingId, List<UUID> passengerIdsToCancel) {
        log.info("Initiating partial/full cancellation for booking: {}", bookingId);

        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already fully cancelled.");
        }

        List<Passenger> allPassengers = passengerRepository.findAllByBooking(booking);

        // 1. Process individual passenger cancellations
        for (Passenger passenger : allPassengers) {
            if (passengerIdsToCancel.contains(passenger.getId()) && passenger.getStatus() != PassengerStatus.CANCELLED) {

                // Free seat and upgrade WL if they were confirmed
                if (passenger.getStatus() == PassengerStatus.CONFIRMED && passenger.getSeatNumber() != null) {
                    Optional<SeatInventory> seatOpt = seatInventoryRepository.findByTrainAndJourneyDateAndCoachAndSeatNumber(
                            booking.getTrain().getTrainNo(),
                            booking.getJourneyDate(), passenger.getCoach(), passenger.getSeatNumber()
                    );
                    seatOpt.ifPresent(freedSeat -> processWaitingListUpgrade(booking, freedSeat));
                }

                // Mark this specific passenger as cancelled
                passenger.setStatus(PassengerStatus.CANCELLED);
                passenger.setSeatNumber(null);
                passenger.setCoach(null);
            }
        }

        // 2. Determine the new overall Booking Status
        boolean allCancelled = allPassengers.stream()
                .allMatch(p -> p.getStatus() == PassengerStatus.CANCELLED);

        if (allCancelled) {
            booking.setStatus(BookingStatus.CANCELLED);
        } else {
            // If at least one person is still travelling, it is partially cancelled
            booking.setStatus(BookingStatus.PARTIALLY_CANCELLED);
        }

        passengerRepository.saveAll(allPassengers);
        bookingRepository.save(booking);

        log.info("Cancellation processed. New Booking Status: {}", booking.getStatus());
    }

    private void processWaitingListUpgrade(Booking cancelledBooking, SeatInventory freedSeat) {
        // Find the next person in line for this train and date
        Optional<Passenger> wlPassengerOpt = passengerRepository.findNextWaitingListPassenger(
                cancelledBooking.getTrain().getTrainNo(),
                cancelledBooking.getJourneyDate()
        );

        if (wlPassengerOpt.isPresent()) {
            Passenger luckyPassenger = wlPassengerOpt.get();
            log.info("Upgrading WL Passenger {} to CONFIRMED for seat {}-{}", 
                    luckyPassenger.getId(), freedSeat.getCoach(), freedSeat.getSeatNumber());

            // Assign the seat to the WL passenger
            luckyPassenger.setStatus(PassengerStatus.CONFIRMED);
            luckyPassenger.setCoach(freedSeat.getCoach());
            luckyPassenger.setSeatNumber(freedSeat.getSeatNumber());
            passengerRepository.save(luckyPassenger);

            // Keep the seat BOOKED, just belonging to someone else now!
            freedSeat.setStatus(SeatStatus.BOOKED);
            seatInventoryRepository.save(freedSeat);

            // Check if the lucky passenger's entire booking is now fully confirmed
            checkAndUpdateBookingStatus(luckyPassenger.getBooking());
            
        } else {
            // Nobody is on the waiting list! Release the seat to the public.
            log.info("No WL passengers found. Releasing seat {}-{}", freedSeat.getCoach(), freedSeat.getSeatNumber());
            freedSeat.setStatus(SeatStatus.AVAILABLE);
            freedSeat.setBlockedAt(null);
            seatInventoryRepository.save(freedSeat);
        }
    }

    private void checkAndUpdateBookingStatus(Booking booking) {
        List<Passenger> allPassengers = passengerRepository.findAllByBooking(booking);
        boolean allConfirmed = allPassengers.stream().allMatch(p -> p.getStatus() == PassengerStatus.CONFIRMED);
        
        if (allConfirmed && booking.getStatus() != BookingStatus.CONFIRMED) {
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);
        }
    }
}