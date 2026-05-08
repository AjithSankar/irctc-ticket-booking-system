package dev.ak.irctc.dto;

import java.util.UUID;

public record PassengerDTO(
        UUID id,
        String name,
        int age,
        String gender,
        String status,
        String coach,
        Integer seatNumber
) {}