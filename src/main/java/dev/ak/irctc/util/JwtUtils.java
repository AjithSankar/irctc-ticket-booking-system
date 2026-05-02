package dev.ak.irctc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
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
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // MODERN JJWT SYNTAX (0.12.0+)
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Replaces setSigningKey()
                .build()
                .parseSignedClaims(token)    // Replaces parseClaimsJws()
                .getPayload();               // Replaces getBody()
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        return createToken(extraClaims, userDetails.getUsername());
    }

    // MODERN JJWT SYNTAX (0.12.0+)
    private String createToken(Map<String, Object> extraClaims, String subject) {
        return Jwts.builder()
                .claims(extraClaims)                         // Replaces setClaims()
                .subject(subject)                            // Replaces setSubject()
                .issuedAt(new Date(System.currentTimeMillis())) // Replaces setIssuedAt()
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Replaces setExpiration()
                .signWith(getSigningKey())                   // Removed SignatureAlgorithm param, it's inferred
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}