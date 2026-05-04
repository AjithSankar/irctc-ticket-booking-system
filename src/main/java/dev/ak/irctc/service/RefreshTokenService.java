package dev.ak.irctc.service;

import dev.ak.irctc.entity.RefreshToken;
import dev.ak.irctc.entity.User;
import dev.ak.irctc.repository.RefreshTokenRepository;
import dev.ak.irctc.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // 7 days in milliseconds
    private final Long REFRESH_TOKEN_DURATION_MS = 604800000L;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user: {}", user.getEmail());

        // Update any existing refresh tokens for the user to ensure only one valid token exists at a time
        log.debug("Checking for existing refresh tokens for user: {}", user.getEmail());
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken;
        if (existingRefreshToken.isPresent()) {
            log.debug("Existing refresh token found, reusing and updating it for user: {}", user.getEmail());
            refreshToken = existingRefreshToken.get();
        } else {
            log.debug("No existing refresh token found, creating new one for user: {}", user.getEmail());
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        log.debug("Setting token expiry date to 7 days from now");
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        refreshToken.setToken(UUID.randomUUID().toString()); // Secure random string

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created successfully for user: {}", user.getEmail());
        return savedToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        log.debug("Verifying expiration for refresh token, expiry date: {}", token.getExpiryDate());
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            log.warn("Refresh token has expired. Current time: {}, Expiry time: {}", Instant.now(), token.getExpiryDate());
            log.debug("Deleting expired refresh token from database");
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh accessToken was expired. Please make a new signin request");
        }
        log.debug("Refresh token is still valid");
        return token;
    }

    public Optional<RefreshToken> findByToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken);
    }
}