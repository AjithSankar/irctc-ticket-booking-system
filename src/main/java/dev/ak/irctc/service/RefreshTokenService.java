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
    private final UserRepository userRepository;

    // 7 days in milliseconds
    private final Long REFRESH_TOKEN_DURATION_MS = 604800000L;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // Update any existing refresh tokens for the user to ensure only one valid token exists at a time
        Optional<RefreshToken> existingRefreshToken = refreshTokenRepository.findByUser(user);
        RefreshToken refreshToken;
        if (existingRefreshToken.isPresent()) {
            refreshToken = existingRefreshToken.get();
        } else {
            refreshToken = new RefreshToken();
            refreshToken.setUser(user);
        }

        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS));
        refreshToken.setToken(UUID.randomUUID().toString()); // Secure random string

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh accessToken was expired. Please make a new signin request");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String requestRefreshToken) {
        return refreshTokenRepository.findByToken(requestRefreshToken);
    }
}