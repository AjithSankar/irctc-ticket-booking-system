package dev.ak.irctc.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email
) {
}
