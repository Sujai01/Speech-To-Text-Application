package com.sttapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Custom filter that intercepts every HTTP request to check for a valid JWT.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Grab the "Authorization" header from the HTTP Request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Check if the header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pass it down the chain. If it's hitting a public endpoint (like /register), it's fine.
            // If it's a protected endpoint, Spring Security will block it later.
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the actual token (ignoring the "Bearer " prefix)
        jwt = authHeader.substring(7);
        
        // 4. Ask JwtService to extract the email from the Token
        userEmail = jwtService.extractUsername(jwt);

        // 5. If an email exists and the user isn't already authenticated in this session...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user from the database
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 6. If the token matches the user and isn't expired
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Create a Security Token letting Spring know this user is officially authenticated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Update the Spring Security Context
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Hand the request off to the next filter in the chain
        filterChain.doFilter(request, response);
    }
}
