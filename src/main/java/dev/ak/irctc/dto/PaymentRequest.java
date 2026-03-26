package dev.ak.irctc.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        UUID bookingId,
        String paymentMethod,
        BigDecimal amount
) {
}
