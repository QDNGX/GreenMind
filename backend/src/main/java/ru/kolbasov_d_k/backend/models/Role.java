package ru.kolbasov_d_k.backend.models;

/**
 * Enum representing the possible roles a user can have in the system.
 * Used for authorization and access control.
 */
public enum Role {
    /**
     * Regular user role with standard permissions.
     */
    USER, 
    
    /**
     * Administrator role with elevated permissions for system management.
     */
    ADMIN
}
