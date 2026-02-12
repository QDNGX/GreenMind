package ru.kolbasov_d_k.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
import ru.kolbasov_d_k.backend.services.UserService;
import ru.kolbasov_d_k.backend.utils.exceptions.DuplicateEmailException;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller responsible for handling user registration.
 * Provides an HTTP endpoint to receive and validate registration data.
 */
@RestController
public class RegisterController {

    private final UserService userService;

    /**
     * Constructs a new RegisterController with the required service.
     *
     * @param userService Service for user-related operations
     */
    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registers a new user in the system.
     * This method now uses centralized exception handling via GlobalExceptionHandler.
     * 
     * Behavior:
     * - If request validation fails, returns 400 with validation error messages (handled by GlobalExceptionHandler).
     * - If a user with the provided email already exists, throws DuplicateEmailException (handled by GlobalExceptionHandler).
     * - On successful user creation, returns 200 with a success message.
     * - Any other exceptions are handled by GlobalExceptionHandler.
     *
     * @param userDTO Data transfer object containing user registration information (username, email, password)
     * @return ResponseEntity with a success message on successful registration
     * @throws DuplicateEmailException if a user with the provided email already exists
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody UserDTO userDTO) {
        if(userService.existsByEmail(userDTO.getEmail())){
            throw new DuplicateEmailException(userDTO.getEmail());
        }

        userService.create(userDTO);
        Map<String, String> success = new HashMap<>();
        success.put("message", "Пользователь зарегистрирован");
        return ResponseEntity.ok(success);
    }
}
