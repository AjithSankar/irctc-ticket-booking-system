package dev.ak.irctc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class JwtUtils {

    // Keep this secret key extremely safe in production (e.g., application.yml / environment variables)
    // Must be at least 256 bits (32 bytes) for HS256
    private static final String SECRET = "4512631236589785451236547896541235698745123654789654123569874512";
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    // Updated to return SecretKey
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        log.debug("Extracting username from JWT token");
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        log.debug("Extracting expiration date from JWT token");
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting specific claim from JWT token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // MODERN JJWT SYNTAX (0.12.0+)
    private Claims extractAllClaims(String token) {
        log.debug("Parsing and extracting all claims from JWT token");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey()) // Replaces setSigningKey()
                    .build()
                    .parseSignedClaims(token)    // Replaces parseClaimsJws()
                    .getPayload();               // Replaces getBody()
        } catch (JwtException e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        log.debug("Checking if JWT token is expired");
        Date expiration = extractExpiration(token);
        boolean isExpired = expiration.before(new Date());
        if (isExpired) {
            log.debug("JWT token has expired");
        } else {
            log.debug("JWT token is still valid");
        }
        return isExpired;
    }

    public String generateToken(UserDetails userDetails) {
        log.info("Generating new JWT token for user: {}", userDetails.getUsername());
        Map<String, Object> extraClaims = new HashMap<>();
        return createToken(extraClaims, userDetails.getUsername());
    }

    // MODERN JJWT SYNTAX (0.12.0+)
    private String createToken(Map<String, Object> extraClaims, String subject) {
        log.debug("Creating JWT token for subject: {}", subject);
        try {
            String token = Jwts.builder()
                    .claims(extraClaims)                         // Replaces setClaims()
                    .subject(subject)                            // Replaces setSubject()
                    .issuedAt(new Date(System.currentTimeMillis())) // Replaces setIssuedAt()
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Replaces setExpiration()
                    .signWith(getSigningKey())                   // Removed SignatureAlgorithm param, it's inferred
                    .compact();
            log.debug("JWT token created successfully for subject: {}", subject);
            return token;
        } catch (Exception e) {
            log.error("Failed to create JWT token for subject {}: {}", subject, e.getMessage(), e);
            throw e;
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        log.debug("Validating JWT token for user: {}", userDetails.getUsername());
        try {
            final String username = extractUsername(token);
            boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
            if (isValid) {
                log.debug("JWT token validation successful for user: {}", userDetails.getUsername());
            } else {
                log.warn("JWT token validation failed for user: {}", userDetails.getUsername());
            }
            return isValid;
        } catch (Exception e) {
            log.warn("JWT token validation failed with error for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }
}