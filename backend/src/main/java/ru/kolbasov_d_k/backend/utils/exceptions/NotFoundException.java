package ru.kolbasov_d_k.backend.utils.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 * Handled by GlobalExceptionHandler which returns 404 status with JSON error message.
 */
public class NotFoundException extends RuntimeException {
    
    /**
     * Constructs a new NotFoundException with a generic error message.
     */
    public NotFoundException() {
        super("Resource not found");
    }

    /**
     * Constructs a new NotFoundException with a specific resource name.
     *
     * @param resourceName The name of the resource that was not found
     */
    public NotFoundException(String resourceName) {
        super(resourceName + " not found");
    }

    /**
     * Constructs a new NotFoundException with a specific resource name and ID.
     *
     * @param resourceName The name of the resource that was not found
     * @param id The ID of the resource that was not found
     */
    public NotFoundException(String resourceName, Object id) {
        super(resourceName + " with id " + id + " not found");
    }

}
