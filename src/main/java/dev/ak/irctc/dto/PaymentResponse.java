package dev.ak.irctc.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(
        UUID bookingId,
        UUID transactionId,
        String paymentStatus,
        BigDecimal amount
) {
}
