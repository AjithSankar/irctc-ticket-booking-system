package dev.ak.irctc.controller;

import dev.ak.irctc.dto.*;
import dev.ak.irctc.entity.RefreshToken;
import dev.ak.irctc.entity.User;
import dev.ak.irctc.repository.UserRepository;
import dev.ak.irctc.service.RefreshTokenService;
import dev.ak.irctc.service.UserService;
import dev.ak.irctc.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Allow requests from Vite
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, JwtUtils jwtUtils, UserService userService, UserRepository userRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        log.info("Attempting login for user: {}", loginRequest.email());
        try {
            // This triggers Spring Security to authenticate against your database
            log.debug("Authenticating user with provided credentials");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
            log.debug("Authentication successful for user: {}", loginRequest.email());
        } catch (BadCredentialsException e) {
            log.warn("Login failed - incorrect credentials for user: {}", loginRequest.email());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect email or password");
        }

        // If authentication passes, load the user and generate a accessToken
        log.debug("Loading user details for: {}", loginRequest.email());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.email());
        final String jwt = jwtUtils.generateToken(userDetails);
        log.debug("JWT token generated successfully for user: {}", loginRequest.email());

        User user = userRepository.findByEmail(loginRequest.email()).orElseThrow(() -> new Exception("User not found"));
        log.debug("Creating refresh token for user: {}", loginRequest.email());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        
        log.info("Login successful for user: {}", loginRequest.email());
        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken.getToken(), userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.email());
        try {
            log.debug("Initiating user registration for email: {}", request.email());
            userService.registerNewUser(request);
            log.info("User registration successful for email: {}", request.email());
            return ResponseEntity.ok("User registered successfully. You can now log in.");
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed - invalid argument for email {}: {}", request.email(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Registration failed - unexpected error for email {}: {}", request.email(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed. Please try again.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        log.info("Token refresh request received");
        String requestRefreshToken = request.refreshToken();
        log.debug("Validating refresh token");

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(token -> {
                    log.debug("Refresh token found, verifying expiration");
                    return refreshTokenService.verifyExpiration(token);
                })
                .map(RefreshToken::getUser)
                .map(user -> {
                    log.debug("Generating new JWT token for user: {}", user.getEmail());
                    String token = jwtUtils.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
                    log.info("Token refreshed successfully for user: {}", user.getEmail());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> {
                    log.error("Token refresh failed - refresh token not found in database");
                    return new RuntimeException("Refresh accessToken is not in database!");
                });
    }
}