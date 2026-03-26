package dev.ak.irctc.dto;

import dev.ak.irctc.entity.Passenger;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookingHoldRequestDTO(
        @NotNull
        Integer trainNumber,
        @NotNull
        String journeyDate,
        @NotNull
        String sourceStation,
        @NotNull
        String destinationStation,
        @NotNull
        String category,
        @NotNull
        String classType,
        @NotNull
        List<Passenger> passengers
) {
}
