package dev.ak.irctc.service;

import dev.ak.irctc.dto.*;
import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Passenger;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.entity.User;
import dev.ak.irctc.enums.BookingStatus;
import dev.ak.irctc.queue.BookingQueue;
import dev.ak.irctc.repository.BookingRepository;
import dev.ak.irctc.repository.PassengerRepository;
import dev.ak.irctc.repository.TrainRepository;
import dev.ak.irctc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final UserRepository userRepository;
    private final BookingQueue bookingQueue;
    private final TrainRepository trainRepository;

    public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository, UserRepository userRepository, BookingQueue bookingQueue, TrainRepository trainRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.userRepository = userRepository;
        this.bookingQueue = bookingQueue;
        this.trainRepository = trainRepository;
    }

    @Transactional
    public BookingResponseDTO createBooking(String idempotencyKey, BookingRequest bookingRequest) {
        log.debug("createBooking {}", idempotencyKey);
        // Idempotency check
        Optional<Booking> optionalBooking = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalBooking.isPresent()) {
            log.info("Duplicate booking request detected for idempotency key: {}", idempotencyKey);
            return mapToBookingResponse(optionalBooking.get());
        }

        User user = userRepository.findById(bookingRequest.userId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + bookingRequest.userId()));
        Train train = trainRepository.findByTrainNo(bookingRequest.trainNumber()).orElseThrow(() -> new IllegalArgumentException("Train not found for train number: " + bookingRequest.trainNumber()));

        // Create booking and save
        Booking booking = Booking.builder()
                .user(user)
                .idempotencyKey(idempotencyKey)
                .train(train)
                .journeyDate(LocalDate.parse(bookingRequest.journeyDate()))
                .status(BookingStatus.INITIATED)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        List<Passenger> passengers = bookingRequest.passengers().stream()
                .map(passenger -> Passenger.builder()
                        .booking(booking)
                        .name(passenger.getName())
                        .age(passenger.getAge())
                        .build())
                .toList();

        passengerRepository.saveAll(passengers);

        // Publish to queue after transaction commits successfully
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        // Enqueue booking for processing
                        bookingQueue.publish(new BookingRequestDTO(
                                booking.getBookingId(),
                                booking.getTrain().getTrainNo(),
                                booking.getIdempotencyKey(),
                                booking.getJourneyDate().toString(),
                                bookingRequest.sourceStation(),
                                bookingRequest.destinationStation(),
                                bookingRequest.category(),
                                bookingRequest.classType(),
                                passengers
                        ));
                    }
                }
        );

        return mapToBookingResponse(booking);
    }

    private static @NonNull BookingResponseDTO mapToBookingResponse(Booking existingBooking) {
        return new BookingResponseDTO(existingBooking.getBookingId(), existingBooking.getStatus().name(), String.valueOf(existingBooking.getTrain().getTrainNo()), existingBooking.getJourneyDate().toString());
    }

    public BookingDetailsDTO findBookingById(UUID bookingId) {
        Booking booking = bookingRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found with ID: " + bookingId
                ));

        return mapToBookingDetailsResponse(booking);
    }

    private BookingDetailsDTO mapToBookingDetailsResponse(Booking booking) {
        List<Passenger> passengers = passengerRepository.findAllByBooking(booking);
        List<PassengerDTO> passengerDTOs = passengers.stream()
                .map(p -> new PassengerDTO(
                        p.getId(),
                        p.getName(),
                        p.getAge(),
                        p.getGender() != null ? p.getGender() : "",
                        p.getStatus() != null ? p.getStatus().name() : "PENDING",
                        p.getCoach(),
                        p.getSeatNumber()
                ))
                .toList();

        return new BookingDetailsDTO(
                booking.getBookingId(),
                booking.getStatus().name(),
                String.valueOf(booking.getTrain().getTrainNo()),
                booking.getJourneyDate().toString(),
                passengerDTOs
        );
    }

    public List<BookingDetailsDTO> getUserBookings(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        List<Booking> bookings = bookingRepository.findAllByUserOrderByCreatedAtDesc(user);

        // Map the list of entities to our DTOs (which already include passenger details!)
        return bookings.stream()
                .map(this::mapToBookingDetailsResponse)
                .toList();
    }

}
