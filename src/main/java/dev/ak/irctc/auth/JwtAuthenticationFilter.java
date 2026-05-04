package dev.ak.irctc.auth;

import dev.ak.irctc.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        log.debug("Processing request for endpoint: {} {}", request.getMethod(), request.getRequestURI());

        String username = null;
        String jwt = null;

        // JWT is typically passed as "Bearer <accessToken>"
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            log.debug("Authorization header found with Bearer token");
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtils.extractUsername(jwt);
                log.debug("Username extracted from JWT: {}", username);
            } catch (Exception e) {
                // Log accessToken extraction failure
                log.warn("Failed to extract username from JWT token: {}", e.getMessage());
            }
        } else {
            log.debug("No valid Authorization header found in request");
        }

        // If username is extracted and no existing authentication context is set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("Loading user details for username: {}", username);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtils.validateToken(jwt, userDetails)) {
                log.debug("JWT token validation successful for user: {}", username);
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Set the user in the Security Context
                log.debug("Setting user in SecurityContext for: {}", username);
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                log.info("User authenticated successfully: {}", username);
            } else {
                log.warn("JWT token validation failed for user: {}", username);
            }
        } else if (username != null) {
            log.debug("Authentication already exists in SecurityContext, skipping authentication");
        }
        filterChain.doFilter(request, response);
    }
}