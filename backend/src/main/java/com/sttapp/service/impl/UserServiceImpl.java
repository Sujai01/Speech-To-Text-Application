package com.sttapp.service.impl;

import com.sttapp.entity.User;
import com.sttapp.repository.UserRepository;
import com.sttapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the UserService.
 * Handles the actual business logic for User operations.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) {
        // We use .orElseThrow() to immediately stop execution and throw an exception 
        // if the user doesn't exist, which our Global Exception Handler will catch later.
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
