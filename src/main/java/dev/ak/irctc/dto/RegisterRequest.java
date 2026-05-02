package dev.ak.irctc.dto;

public record RegisterRequest(
        String fullName,
        Integer age,
        String gender,
        String dateOfBirth,
        String email,
        String password,
        String country
) {
}
