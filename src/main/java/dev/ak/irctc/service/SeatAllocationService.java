package dev.ak.irctc.service;

import dev.ak.irctc.dto.BookingRequestDTO;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class SeatAllocationService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentService paymentService;

    public SeatAllocationService(SeatInventoryRepository seatInventoryRepository, BookingRepository bookingRepository, PassengerRepository passengerRepository, PaymentService paymentService) {
        this.seatInventoryRepository = seatInventoryRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.paymentService = paymentService;
    }


    @Transactional
    public void allocateSeats(BookingRequestDTO bookingRequestDTO) {
        log.info("Allocating seats for request: {} , IdempotencyKey={}", bookingRequestDTO.bookingId(), bookingRequestDTO.idempotencyKey());
        Booking booking = bookingRepository.findByBookingId(bookingRequestDTO.bookingId()).orElseThrow(() -> new RuntimeException("booking id:" + bookingRequestDTO.bookingId()+ " not found"));
        booking.setStatus(BookingStatus.PROCESSING);

        List<Passenger> passengers = passengerRepository.findAllByBooking(booking);
        int requestedSeats = passengers.size();

        // 🔹 STEP 1: LOCK SEATS (single DB call)
        List<SeatInventory> lockedSeats = seatInventoryRepository.findAvailableSeatsAndLock(
                bookingRequestDTO.trainNumber(),
                LocalDate.parse(bookingRequestDTO.journeyDate()),
                requestedSeats
        );

        int confirmedSeats = lockedSeats.size();

        // Step 2: Decide outcome
        if (confirmedSeats == 0) {
            failBooking(booking, "No seats available, all passengers in waiting list. IdempotencyKey=" + bookingRequestDTO.idempotencyKey());
            return;
        }

        // Step 3: Mark Seats Blocked
        markSeatsBooked(lockedSeats, bookingRequestDTO.idempotencyKey());

        // 🔹 STEP 4: Assign passengers
        assignPassengers(passengers, lockedSeats, bookingRequestDTO.bookingId().toString(), bookingRequestDTO.idempotencyKey());

        passengerRepository.saveAll(passengers);

        // STEP 5: Payment (only for confirmed seats)
        double amount = calculateAmount(confirmedSeats);
        boolean paymentSuccess = paymentService.processPayment(booking.getBookingId(), amount, booking.getIdempotencyKey());

        if (!paymentSuccess) {
            log.info("Payment failed for booking {}, IdempotencyKey={}", booking.getBookingId(), bookingRequestDTO.idempotencyKey());
            releaseSeats(lockedSeats, bookingRequestDTO.idempotencyKey());
            booking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(booking);
            unassignPassengers(passengers, "Payment failed so Unassigned passengers. IdempotencyKey=" + bookingRequestDTO.idempotencyKey());
            return;
        }

        // 🔹 STEP 6: Finalize booking
        finalizeBooking(booking, passengers, lockedSeats);
    }

    private void unassignPassengers(List<Passenger> passengers, String reason) {
        for (Passenger passenger : passengers) {
            passenger.setSeatNumber(null);
            passenger.setCoach(null);
            passenger.setStatus(null);
            passenger.setBerthType(null);
        }
        passengerRepository.saveAll(passengers);
        log.info(reason);
    }

    private double calculateAmount(int confirmedCount) {
        return confirmedCount * 500.00;
    }

    private void finalizeBooking(Booking booking, List<Passenger> passengers, List<SeatInventory> seats) {

        seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
        seatInventoryRepository.saveAll(seats);

        boolean allConfirmed = passengers.stream()
                .allMatch(p -> p.getStatus() == PassengerStatus.CONFIRMED);

        booking.setStatus(allConfirmed ? BookingStatus.CONFIRMED : BookingStatus.PARTIALLY_CONFIRMED);

        bookingRepository.save(booking);
        log.info("Booking has been finalized for booking {}, IdempotencyKey={}", booking.getBookingId(), booking.getIdempotencyKey());
    }

    private void assignPassengers(List<Passenger> passengers, List<SeatInventory> seats, String bookingId, String idempotencyKey) {

        log.info("Assigning seats to passengers for booking {}, IdempotencyKey={}", bookingId, idempotencyKey);
        int confirmed = seats.size();
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = passengers.get(i);
            if (i < confirmed) {
                SeatInventory s = seats.get(i);
                p.setSeatNumber(s.getSeatNumber());
                p.setCoach(s.getCoach());
                p.setBerthType("LOWER");
                p.setStatus(PassengerStatus.CONFIRMED);
            } else {
                p.setStatus(PassengerStatus.WAITING_LIST);
            }
        }
    }

    private void failBooking(Booking booking, String reason) {
        log.info("Failing booking {} due to {}. IdempotencyKey={}", booking.getBookingId(), reason, booking.getIdempotencyKey());
        booking.setStatus(BookingStatus.NOT_BOOKED);
        bookingRepository.save(booking);
    }

    private void releaseSeats(List<SeatInventory> seats, String idempotencyKey) {
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setBlockedAt(null);
        });
        seatInventoryRepository.saveAll(seats);

        log.info("Released {} seats. IdempotencyKey={}", seats.size(), idempotencyKey);
    }

    private void markSeatsBooked(List<SeatInventory> seats, String idempotencyKey) {
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.BOOKED);
            seat.setBlockedAt(LocalDateTime.now());
        });
        seatInventoryRepository.saveAll(seats);
        log.info("Marked {} seats to booked. IdempotencyKey={}", seats.size(), idempotencyKey);
    }
}
