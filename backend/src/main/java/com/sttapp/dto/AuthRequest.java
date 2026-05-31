package com.sttapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling incoming login requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be properly formatted")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
