package com.sttapp.service;

import com.sttapp.dto.AuthRequest;
import com.sttapp.dto.AuthResponse;
import com.sttapp.dto.RegisterRequest;

/**
 * Service interface for handling Authentication and Registration logic.
 */
public interface AuthService {

    /**
     * Registers a new user into the system.
     *
     * @param request the user's registration details (name, email, password)
     * @return AuthResponse containing the generated JWT token and user profile details
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates an existing user and generates a token.
     *
     * @param request the user's login credentials (email, password)
     * @return AuthResponse containing the generated JWT token and user profile details
     */
    AuthResponse login(AuthRequest request);
}
