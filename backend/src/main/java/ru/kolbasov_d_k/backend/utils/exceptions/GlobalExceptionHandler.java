package ru.kolbasov_d_k.backend.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the entire application.
 * Centralizes error handling and provides consistent JSON responses to clients.
 * This class handles various types of exceptions and converts them to appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Bean Validation errors (@Valid annotation violations).
     * Prioritizes NotBlank validation errors by showing only those when present,
     * otherwise shows all other validation errors. This provides cleaner error
     * messages for users by focusing on empty field errors first.
     * Returns the same format as RegisterController for compatibility with existing frontend code.
     * 
     * @param ex The MethodArgumentNotValidException containing validation errors
     * @return ResponseEntity with status 400 Bad Request and a map containing concatenated error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        java.util.List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        
        // Разделение вывод ошибок на пустые поля в формах и остальные
        java.util.List<FieldError> notBlankErrors = fieldErrors.stream()
                .filter(error -> error.getCode() != null && error.getCode().equals("NotBlank"))
                .toList();
        String errorMessages;
        if (!notBlankErrors.isEmpty()) {
            // Если есть пустые поля в форме, показываем только их
            errorMessages = notBlankErrors.stream()
                    .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error")
                    .collect(Collectors.joining("\n"));
        } else {
            // Если нет пустых полей, показываем остальные ошибки (формат, длина и т.д.)
            errorMessages = fieldErrors.stream()
                    .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Validation error")
                    .collect(Collectors.joining("\n"));
        }
        
        Map<String, String> error = new HashMap<>();
        error.put("error", errorMessages);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles "Resource not found" exceptions.
     * Returns a standardized error response when requested resources cannot be found.
     * 
     * @param ex The NotFoundException containing the error message
     * @return ResponseEntity with status 404 Not Found and a JSON error message
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles "Limit exceeded" exceptions.
     * Returns an error response when operations exceed predefined limits (e.g., cart capacity, quantity limits).
     * 
     * @param ex The OverLimitException containing the error message
     * @return ResponseEntity with status 409 Conflict and a JSON error message
     */
    @ExceptionHandler(OverLimitException.class)
    public ResponseEntity<Map<String, String>> handleOverLimit(OverLimitException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles all other unexpected exceptions that are not caught by specific handlers.
     * Returns a generic error message without exposing internal system details for security reasons.
     * 
     * @param ex The unhandled exception
     * @return ResponseEntity with status 500 Internal Server Error and a generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}