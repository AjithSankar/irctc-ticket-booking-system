package dev.ak.irctc.dto;

public record PassengerDTO(
        String name,
        int age,
        String gender,
        String status,
        String coach,
        Integer seatNumber
) {}