package dev.ak.irctc.dto;

import dev.ak.irctc.entity.Passenger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookingConfirmationResponse(
        UUID bookingId,
        String status,
        String pnrNumber,
        List<Passenger> passengers,
        LocalDateTime bookingTime
) {
}
