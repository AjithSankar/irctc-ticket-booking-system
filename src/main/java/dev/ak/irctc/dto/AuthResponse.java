package dev.ak.irctc.dto;

public record AuthResponse(
        String token,
        String email
) {
}
