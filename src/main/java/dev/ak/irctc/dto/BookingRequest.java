package dev.ak.irctc.dto;

import dev.ak.irctc.entity.Passenger;

import java.util.List;

public record BookingRequest(
        Long userId,
        Integer trainNumber,
        String journeyDate,
        String sourceStation,
        String destinationStation,
        String category,
        String classType,
        List<Passenger> passengers
) {
}
