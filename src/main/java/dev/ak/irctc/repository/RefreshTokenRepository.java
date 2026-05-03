package dev.ak.irctc.repository;

import dev.ak.irctc.entity.RefreshToken;
import dev.ak.irctc.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);

    Optional<RefreshToken> findByUser(User user);
}