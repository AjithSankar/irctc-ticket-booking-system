package dev.ak.irctc.dto;

import java.time.LocalDate;

public record ClassAvailabilityDTO(
    LocalDate date,
    String availabilityStatus
) {}