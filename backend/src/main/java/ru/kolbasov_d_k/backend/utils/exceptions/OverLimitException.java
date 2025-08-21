package ru.kolbasov_d_k.backend.utils.exceptions;

/**
 * Exception thrown when a requested quantity exceeds the available limit.
 * Handled by GlobalExceptionHandler which returns 409 status with JSON error message.
 */
public class OverLimitException extends RuntimeException {

    /**
     * Constructs a new OverLimitException with a generic error message.
     */
    public OverLimitException() {
        super("Limit exceeded");
    }

    /**
     * Constructs a new OverLimitException with a custom error message.
     *
     * @param message The custom error message
     */
    public OverLimitException(String message) {
        super(message);
    }

    /**
     * Constructs a new OverLimitException with a formatted error message that includes
     * the resource name, requested quantity, and available quantity.
     *
     * @param resourceName The name of the resource
     * @param requested The requested quantity
     * @param available The available quantity
     */
    public OverLimitException(String resourceName, int requested, int available) {
        super(String.format(
                "Cannot reserve %d %s (only %d left in stock)",
                requested, resourceName, available));
    }
}
