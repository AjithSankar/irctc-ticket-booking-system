package dev.ak.irctc.dto;

public record CheckoutSummaryDTO(
    Integer trainNo,
    String trainName,
    String sourceStation,
    String destinationStation,
    String departureTime,
    String arrivalTime,
    String duration,
    String classType,
    double baseFare,
    String srcStationName,
    String destStationName
) {}