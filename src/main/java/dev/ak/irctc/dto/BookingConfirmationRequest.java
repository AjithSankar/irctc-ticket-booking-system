package dev.ak.irctc.dto;

import java.util.UUID;

public record BookingConfirmationRequest(
        UUID bookingId,
        UUID transactionId,
        String paymentStatus
) {
}
