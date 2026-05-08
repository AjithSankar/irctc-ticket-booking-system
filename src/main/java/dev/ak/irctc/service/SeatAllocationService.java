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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class SeatAllocationService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final PaymentService paymentService;

    // 🔹 FIX: Self-injection to properly handle Spring's internal @Transactional proxies
    private final SeatAllocationService self;

    public SeatAllocationService(SeatInventoryRepository seatInventoryRepository, BookingRepository bookingRepository,
                                 PassengerRepository passengerRepository, PaymentService paymentService,
                                 @Lazy SeatAllocationService self) {
        this.seatInventoryRepository = seatInventoryRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.paymentService = paymentService;
        this.self = self;
    }


    public void allocateSeats(BookingRequestDTO bookingRequestDTO) {

        try {

            log.info("Allocating seats for request: {} , IdempotencyKey={}", bookingRequestDTO.bookingId(), bookingRequestDTO.idempotencyKey());
            // STEP 1: Lock seats and assign passengers (Opens and closes DB transaction)
            List<SeatInventory> lockedSeats = self.blockSeatsTransactionally(bookingRequestDTO);

            if (lockedSeats == null || lockedSeats.isEmpty()) {
                self.failBookingTransactionally(bookingRequestDTO.bookingId(), BookingStatus.NOT_BOOKED, "No seats available, all passengers in waiting list. IdempotencyKey=" + bookingRequestDTO.idempotencyKey());
                return;
            }

            // STEP 2: Payment Gateway Call (NO DATABASE LOCKS HELD DURING THIS NETWORK CALL!)
            double amount = calculateAmount(lockedSeats.size(), bookingRequestDTO.classType());
            boolean paymentSuccess = paymentService.processPayment(
                    bookingRequestDTO.bookingId(),
                    amount,
                    bookingRequestDTO.idempotencyKey()
            );

            // STEP 3: Finalize or Revert (Opens and closes a new DB transaction)
            if (paymentSuccess) {
                self.finalizeBookingTransactionally(bookingRequestDTO.bookingId(), lockedSeats);
            } else {
                log.info("Payment failed for booking {}, IdempotencyKey={}", bookingRequestDTO.bookingId(), bookingRequestDTO.idempotencyKey());
                self.releaseSeatsTransactionally(bookingRequestDTO.bookingId(), lockedSeats, bookingRequestDTO.idempotencyKey());
            }
        } catch (Exception e) {
            log.error("CRITICAL: Unhandled exception during seat allocation for booking {}: {}", bookingRequestDTO.bookingId(), e.getMessage(), e);
            try {
                self.failBookingTransactionally(bookingRequestDTO.bookingId(), BookingStatus.FAILED, "System error during processing: IdempotencyKey=" + bookingRequestDTO.idempotencyKey());
            } catch (Exception ex) {
                log.error("CRITICAL: Failed to update booking status to FAILED for booking {}: {}", bookingRequestDTO.bookingId(), ex.getMessage(), ex);
            }
        }
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

    // --- TRANSACTIONAL BOUNDARY METHODS ---

    @Transactional
    public List<SeatInventory> blockSeatsTransactionally(BookingRequestDTO dto) {

        Booking booking = bookingRepository.findByBookingId(dto.bookingId())
                .orElseThrow(() -> new RuntimeException("booking id:" + dto.bookingId() + " not found"));
        booking.setStatus(BookingStatus.PROCESSING);

        List<Passenger> passengers = passengerRepository.findAllByBooking(booking);
        int requestedSeats = passengers.size();

        // 1. Lock Seats via FOR UPDATE SKIP LOCKED
        List<SeatInventory> lockedSeats = seatInventoryRepository.findAvailableSeatsAndLock(
                dto.trainNumber(),
                LocalDate.parse(dto.journeyDate()),
                requestedSeats
        );

        if (!lockedSeats.isEmpty()) {
            // 2. Temporarily set to BLOCKED
            lockedSeats.forEach(seat -> {
                seat.setStatus(SeatStatus.BLOCKED);
                seat.setBlockedAt(LocalDateTime.now());
            });
            seatInventoryRepository.saveAll(lockedSeats);

            // 3. Assign to Passengers
            assignPassengers(passengers, lockedSeats, dto.bookingId().toString(), dto.idempotencyKey());
            passengerRepository.saveAll(passengers);
        } else {

        }

        bookingRepository.save(booking);
        return lockedSeats;
    }

    @Transactional
    public void finalizeBookingTransactionally(UUID bookingId, List<SeatInventory> seats) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("booking id:" + bookingId + " not found"));

        // Change BLOCKED to BOOKED
        seats.forEach(seat -> seat.setStatus(SeatStatus.BOOKED));
        seatInventoryRepository.saveAll(seats);

        List<Passenger> passengers = passengerRepository.findAllByBooking(booking);
        boolean allConfirmed = passengers.stream()
                .allMatch(p -> p.getStatus() == PassengerStatus.CONFIRMED);

        booking.setStatus(allConfirmed ? BookingStatus.CONFIRMED : BookingStatus.PARTIALLY_CONFIRMED);
        bookingRepository.save(booking);

        log.info("Booking has been finalized for booking {}, IdempotencyKey={}", bookingId, booking.getIdempotencyKey());
    }

    @Transactional
    public void releaseSeatsTransactionally(UUID bookingId, List<SeatInventory> seats, String idempotencyKey) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("booking id:" + bookingId + " not found"));

        // Release seats back to pool
        seats.forEach(seat -> {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setBlockedAt(null);
        });
        seatInventoryRepository.saveAll(seats);

        // Fail the booking and unassign passengers
        booking.setStatus(BookingStatus.FAILED);
        bookingRepository.save(booking);

        List<Passenger> passengers = passengerRepository.findAllByBooking(booking);
        for (Passenger passenger : passengers) {
            passenger.setSeatNumber(null);
            passenger.setCoach(null);
            passenger.setStatus(null);
            passenger.setBerthType(null);
        }
        passengerRepository.saveAll(passengers);

        log.info("Released {} seats. Payment failed so unassigned passengers. IdempotencyKey={}", seats.size(), idempotencyKey);
    }

    @Transactional
    public void failBookingTransactionally(UUID bookingId, BookingStatus status, String reason) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new RuntimeException("booking id:" + bookingId + " not found"));
        log.info("Failing booking {} due to {}", bookingId, reason);
        booking.setStatus(status);
        bookingRepository.save(booking);
    }

    // --- HELPER METHODS ---

    // 🔹 FIX: Dynamic Pricing based on requested Class Type
    private double calculateAmount(int confirmedCount, String classType) {
        double baseFare = 350.00; // Default Sleeper
        if ("3A".equalsIgnoreCase(classType)) baseFare = 950.00;
        if ("2A".equalsIgnoreCase(classType)) baseFare = 1350.00;

        return confirmedCount * baseFare;
    }

}
