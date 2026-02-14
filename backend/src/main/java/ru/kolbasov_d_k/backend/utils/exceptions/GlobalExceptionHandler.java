package ru.kolbasov_d_k.backend.utils.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Global exception handler for the entire application.
 * Centralizes error handling and provides consistent JSON responses to clients.
 * This class handles various types of exceptions and converts them to appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
                    .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Ошибка валидации")
                    .collect(Collectors.joining("\n"));
        } else {
            // Если нет пустых полей, показываем остальные ошибки (формат, длина и т.д.)
            errorMessages = fieldErrors.stream()
                    .map(error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Ошибка валидации")
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
     * Handles duplicate email exceptions during user registration.
     * Returns an error response when trying to register with an email that already exists.
     * 
     * @param ex The DuplicateEmailException containing the error message
     * @return ResponseEntity with status 409 Conflict and a JSON error message
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateEmail(DuplicateEmailException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Данный email уже используется");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles Spring Security authentication failures.
     * Returns an error response when user credentials are incorrect or user is not found.
     * 
     * @param ex The UsernameNotFoundException containing the error message
     * @return ResponseEntity with status 401 Unauthorized and a JSON error message
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFound(UsernameNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Неверные учетные данные");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles IllegalArgumentException for invalid method arguments.
     * Returns an error response when method arguments are invalid or null.
     * 
     * @param ex The IllegalArgumentException containing the error message
     * @return ResponseEntity with status 400 Bad Request and a JSON error message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage() != null ? ex.getMessage() : "Неверные данные запроса");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles unauthorized access attempts.
     * Returns an error response when a user is not authenticated.
     *
     * @param ex The UnauthorizedClientException containing the error message
     * @return ResponseEntity with status 401 Unauthorized and a JSON error message
     */
    @ExceptionHandler(UnauthorizedClientException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedClient(UnauthorizedClientException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Пользователь не авторизован");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles NoSuchElementException when optional elements are not found.
     * Returns an error response when trying to access non-existent elements.
     * 
     * @param ex The NoSuchElementException
     * @return ResponseEntity with status 404 Not Found and a JSON error message
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(NoSuchElementException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Запрашиваемый ресурс не найден");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles database integrity violations (e.g., duplicate keys, foreign key constraints).
     * Returns an error response when database constraints are violated.
     * 
     * @param ex The DataIntegrityViolationException
     * @return ResponseEntity with status 409 Conflict and a JSON error message
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, String> error = new HashMap<>();
        String message = "Нарушение целостности данных";
        
        // Check for specific constraint violations
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("email") && ex.getMessage().contains("unique")) {
                message = "Данный email уже используется";
            }
        }
        
        error.put("error", message);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles malformed JSON requests that cannot be parsed.
     * Returns an error response when request body contains invalid JSON.
     * 
     * @param ex The HttpMessageNotReadableException
     * @return ResponseEntity with status 400 Bad Request and a JSON error message
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Неверный формат запроса");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles unsupported HTTP methods.
     * Returns an error response when client uses HTTP method not supported by the endpoint.
     * 
     * @param ex The HttpRequestMethodNotSupportedException
     * @return ResponseEntity with status 405 Method Not Allowed and a JSON error message
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Метод " + ex.getMethod() + " не поддерживается");
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error);
    }

    /**
     * Handles missing required request parameters.
     * Returns an error response when required request parameters are not provided.
     * 
     * @param ex The MissingServletRequestParameterException
     * @return ResponseEntity with status 400 Bad Request and a JSON error message
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Отсутствует обязательный параметр: " + ex.getParameterName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles type mismatch exceptions for request parameters.
     * Returns an error response when request parameter types don't match expected types.
     * 
     * @param ex The MethodArgumentTypeMismatchException
     * @return ResponseEntity with status 400 Bad Request and a JSON error message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Неверный тип параметра: " + ex.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles Spring Security access denied exceptions.
     * Returns an error response when user doesn't have sufficient privileges.
     * 
     * @param ex The AccessDeniedException
     * @return ResponseEntity with status 403 Forbidden and a JSON error message
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Доступ запрещен");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
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
        log.error("Unhandled exception: {}", ex.getClass().getName(), ex);
        Map<String, String> error = new HashMap<>();
        error.put("error", "Внутренняя ошибка сервера");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}