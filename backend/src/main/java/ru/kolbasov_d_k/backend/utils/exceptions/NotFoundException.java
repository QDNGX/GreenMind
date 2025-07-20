package ru.kolbasov_d_k.backend.utils.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Resource not found");
    }

    public NotFoundException(String resourceName) {
        super(resourceName + " not found");
    }

    public NotFoundException(String resourceName, Object id) {
        super(resourceName + " with id " + id + " not found");
    }

}
