package dev.ak.irctc.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDTO(
        UUID bookingId,
        String status
) {
}
