package dev.ak.irctc.dto;

import dev.ak.irctc.entity.Passenger;

import java.util.List;
import java.util.UUID;

public record BookingRequestDTO(
        UUID bookingId,
        Integer trainNumber,
        String idempotencyKey,
        String journeyDate,
        String sourceStation,
        String destinationStation,
        String category,
        String classType,
        List<Passenger> passengers
) {
}
