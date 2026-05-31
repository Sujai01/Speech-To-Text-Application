package com.sttapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for outgoing authentication responses.
 * Returned to the client upon successful login or registration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    // The JWT token that the frontend will store and send with subsequent requests
    private String token;

    // Basic user details returned so the React frontend can populate the user profile/dashboard immediately
    private Long id;
    private String name;
    private String email;
    private String role;
}
