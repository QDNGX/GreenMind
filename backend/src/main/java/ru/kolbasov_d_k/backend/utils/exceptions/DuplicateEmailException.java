package ru.kolbasov_d_k.backend.utils.exceptions;

/**
 * Exception thrown when attempting to register a user with an email that already exists in the system.
 * This exception provides more specific error handling for duplicate email scenarios
 * compared to generic database integrity violations.
 */
public class DuplicateEmailException extends RuntimeException {
    
    /**
     * Constructs a new DuplicateEmailException with a default message.
     * 
     * @param email The email address that caused the duplication
     */
    public DuplicateEmailException(String email) {
        super("Данный email уже используется: " + email);
    }
    
    /**
     * Constructs a new DuplicateEmailException with a custom message.
     * 
     * @param message The custom error message
     */
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}