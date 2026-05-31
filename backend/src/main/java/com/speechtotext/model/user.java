package com.speechtotext.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * User entity — maps to the "users" table.
 *
 * Implements UserDetails so Spring Security can use this
 * entity directly as its authentication principal, without
 * needing a separate UserDetailsService wrapper object.
 *
 * Design decisions:
 *  - email is the username (unique identifier)
 *  - password field stores BCrypt hash ONLY — never plaintext
 *  - All account flags return true (can be made configurable later)
 *  - @Builder requires @AllArgsConstructor; JPA requires @NoArgsConstructor
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_users_email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    // ── Primary Key ─────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Core Fields ─────────────────────────────────────────────
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /**
     * IMPORTANT: Always store BCrypt hash, never plaintext.
     * Length 255 accommodates BCrypt's 60-char output with room
     * for future algorithm changes (e.g., Argon2 ~95 chars).
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    // ── Role ────────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private Role role = Role.USER;

    // ── Audit ───────────────────────────────────────────────────
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ════════════════════════════════════════════════════════════
    // UserDetails contract implementation
    // ════════════════════════════════════════════════════════════

    /**
     * Returns the role as a Spring Security GrantedAuthority.
     * Prefix "ROLE_" is required for hasRole() SpEL expressions.
     * Example: Role.USER → "ROLE_USER"
     */
    @Override
    public Collection getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /**
     * Spring Security uses this as the username.
     * We use email as the unique identifier.
     */
    @Override
    public String getUsername() {
        return email;
    }

    /**
     * Returns the BCrypt-hashed password.
     * Spring Security compares this against the provided
     * raw password using the configured PasswordEncoder.
     */
    @Override
    public String getPassword() {
        return password;
    }

    // ── Account status flags ─────────────────────────────────────
    // All return true for now. Can be driven by DB columns later
    // (e.g., emailVerified, accountLocked, subscriptionActive).

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
