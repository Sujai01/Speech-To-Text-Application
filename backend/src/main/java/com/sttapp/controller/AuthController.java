package com.sttapp.controller;

import com.sttapp.dto.AuthRequest;
import com.sttapp.dto.AuthResponse;
import com.sttapp.dto.RegisterRequest;
import com.sttapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing public endpoints for Registration and Login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Registers a new user.
     * POST /api/auth/register
     * 
     * @param request JSON payload mapping to RegisterRequest
     * @return 200 OK with AuthResponse (JWT Token) if successful
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        // The @Valid annotation triggers Spring Validation (checking @NotBlank, @Email, @Size in the DTO)
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticates an existing user.
     * POST /api/auth/login
     *
     * @param request JSON payload mapping to AuthRequest
     * @return 200 OK with AuthResponse (JWT Token) if successful
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
