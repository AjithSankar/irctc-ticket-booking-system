package dev.ak.irctc.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingHoldResponseDTO(
        UUID bookingId,
        String status,
        LocalDateTime expiresAt,
        String paymentUrl
) {
}
