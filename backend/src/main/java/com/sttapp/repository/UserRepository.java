package com.sttapp.repository;

import com.sttapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides out-of-the-box CRUD operations and custom query methods for the users table.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     * This is critical for our login process and Spring Security integration.
     *
     * @param email the user's email address
     * @return an Optional containing the User if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user already exists with the given email.
     * Used during registration to prevent duplicate accounts.
     *
     * @param email the user's email address
     * @return true if the email is already taken, false otherwise
     */
    boolean existsByEmail(String email);
}
