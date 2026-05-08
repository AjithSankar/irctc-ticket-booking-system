package dev.ak.irctc.dto;

import java.util.List;
import java.util.UUID;

public record BookingDetailsDTO(
        UUID bookingId,
        String status,
        String trainNumber,
        String journeyDate,
        List<PassengerDTO> passengers
) {
}


