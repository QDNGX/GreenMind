package ru.kolbasov_d_k.backend.utils.exceptions;

/**
 * Exception thrown when a user attempts to access a resource without being authenticated.
 * Handled by GlobalExceptionHandler which returns 401 status with JSON error message.
 */
public class UnauthorizedClientException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedClientException with a specific error message.
     *
     * @param message The error message describing the unauthorized access
     */
    public UnauthorizedClientException(String message) {
        super(message);
    }
}
