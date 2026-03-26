package dev.ak.irctc.dto;

import java.util.UUID;

public record QueuedBookingRequest(
        UUID trackingId,
        Long userId,
        BookingRequestDTO payload
) {}