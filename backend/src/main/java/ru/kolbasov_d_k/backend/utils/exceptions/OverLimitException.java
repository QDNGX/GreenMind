package ru.kolbasov_d_k.backend.utils.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested quantity exceeds the available limit.
 * This exception is annotated with @ResponseStatus(HttpStatus.CONFLICT),
 * which means that when it's thrown in a controller, the response will have a 409 status code.
 */
@ResponseStatus(HttpStatus.CONFLICT)
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
