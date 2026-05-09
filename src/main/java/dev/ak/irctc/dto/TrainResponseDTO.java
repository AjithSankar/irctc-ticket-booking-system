package dev.ak.irctc.dto;

public record TrainResponseDTO(
        Long id,
        Integer trainNo,
        String trainName,
        String sourceStation,
        String destinationStation,
        String runsOn,
        boolean isActive) {
}
