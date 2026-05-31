package com.speechtotext.model;

/**
 * Defines user roles for Role-Based Access Control (RBAC).
 *
 * Spring Security expects roles to be prefixed with "ROLE_"
 * when using hasRole() expressions. We store "USER" / "ADMIN"
 * and prefix in getAuthorities() inside User.java.
 */
public enum Role {
    USER,
    ADMIN
}
