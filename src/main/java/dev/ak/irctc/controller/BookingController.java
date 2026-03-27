package dev.ak.irctc.controller;

import dev.ak.irctc.dto.BookingRequest;
import dev.ak.irctc.dto.BookingResponseDTO;
import dev.ak.irctc.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/bookings")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequest bookingRequest,
                                            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        if (idempotencyKey.isEmpty()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        BookingResponseDTO bookingResponseDTO = bookingService.createBooking(idempotencyKey, bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponseDTO);
    }
}
