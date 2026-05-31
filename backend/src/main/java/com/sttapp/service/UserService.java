package com.sttapp.service;

import com.sttapp.entity.User;

/**
 * Service interface for handling User-related business logic.
 * Adhering to Clean Architecture, we define the contract here.
 */
public interface UserService {

    /**
     * Retrieves a user based on their email address.
     *
     * @param email the email of the user to find
     * @return the User entity
     */
    User getUserByEmail(String email);

    /**
     * Retrieves a user based on their unique ID.
     *
     * @param id the ID of the user
     * @return the User entity
     */
    User getUserById(Long id);
}
