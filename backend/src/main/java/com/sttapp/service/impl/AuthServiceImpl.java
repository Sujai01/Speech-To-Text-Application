package com.sttapp.service.impl;

import com.sttapp.constant.Role;
import com.sttapp.dto.AuthRequest;
import com.sttapp.dto.AuthResponse;
import com.sttapp.dto.RegisterRequest;
import com.sttapp.entity.User;
import com.sttapp.repository.UserRepository;
import com.sttapp.security.JwtService;
import com.sttapp.service.AuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of the AuthService.
 * Handles the business logic for User Authentication and Registration.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 1. Check if email already exists to prevent duplicates
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }

        // 2. Create the User entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // Securely hash the password before saving to the DB using BCrypt
                .password(passwordEncoder.encode(request.getPassword())) 
                .role(Role.USER) // Default role for new signups
                .build();

        // 3. Save user to the database
        userRepository.save(user);

        // 4. Generate actual JWT Token
        String jwtToken = jwtService.generateToken(user); 

        // 5. Return the standardized response DTO
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        // 1. Authenticate user credentials
        // Spring Security handles checking the raw password against the hashed DB password internally
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Fetch the user from the database
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 3. Generate actual JWT Token
        String jwtToken = jwtService.generateToken(user);

        // 4. Return the standardized response DTO
        return AuthResponse.builder()
                .token(jwtToken)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
