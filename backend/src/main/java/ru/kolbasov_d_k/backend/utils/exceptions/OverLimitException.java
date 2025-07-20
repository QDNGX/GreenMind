package ru.kolbasov_d_k.backend.utils.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OverLimitException extends RuntimeException {

    public OverLimitException() {
        super("Limit exceeded");
    }

    public OverLimitException(String message) {
        super(message);
    }

    public OverLimitException(String resourceName, int requested, int available) {
        super(String.format(
                "Cannot reserve %d %s (only %d left in stock)",
                requested, resourceName, available));
    }
}
