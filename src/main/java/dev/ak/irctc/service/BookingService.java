package dev.ak.irctc.service;

import dev.ak.irctc.dto.BookingRequest;
import dev.ak.irctc.dto.BookingRequestDTO;
import dev.ak.irctc.dto.BookingResponseDTO;
import dev.ak.irctc.entity.Booking;
import dev.ak.irctc.entity.Passenger;
import dev.ak.irctc.entity.Train;
import dev.ak.irctc.enums.BookingStatus;
import dev.ak.irctc.queue.BookingQueue;
import dev.ak.irctc.repository.BookingRepository;
import dev.ak.irctc.repository.PassengerRepository;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final BookingQueue bookingQueue;

    public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository, BookingQueue bookingQueue) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.bookingQueue = bookingQueue;
    }

    @Transactional
    public BookingResponseDTO createBooking(String idempotencyKey, BookingRequest bookingRequest) {

        // Idempotency check
        Optional<Booking> optionalBooking = bookingRepository.findByIdempotencyKey(idempotencyKey);
        if (optionalBooking.isPresent()) {
            log.info("Duplicate booking request detected for idempotency key: {}", idempotencyKey);
            return mapToBookingResponse(optionalBooking.get());
        }

        // Create booking and save
        Booking booking = Booking.builder()
                .bookingId(UUID.randomUUID())
                .idempotencyKey(idempotencyKey)
                .train(Train.builder().trainNo(bookingRequest.trainNumber()).build())
                .journeyDate(LocalDate.parse(bookingRequest.journeyDate()))
                .status(BookingStatus.INITIATED)
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

        // Enqueue booking for processing
        bookingQueue.publish(new BookingRequestDTO(
                booking.getBookingId(),
                booking.getTrain().getTrainNo(),
                booking.getJourneyDate().toString(),
                bookingRequest.sourceStation(),
                bookingRequest.destinationStation(),
                bookingRequest.category(),
                bookingRequest.classType(),
                passengers
        ));


        return mapToBookingResponse(booking);
    }

    private static @NonNull BookingResponseDTO mapToBookingResponse(Booking existingBooking) {
        return new BookingResponseDTO(existingBooking.getBookingId(), existingBooking.getStatus().name());
    }

}
