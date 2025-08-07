package ru.kolbasov_d_k.backend.utils.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * This exception is annotated with @ResponseStatus(HttpStatus.NOT_FOUND),
 * which means that when it's thrown in a controller, the response will have a 404 status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
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
