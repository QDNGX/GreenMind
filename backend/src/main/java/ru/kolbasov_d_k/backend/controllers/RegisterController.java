package ru.kolbasov_d_k.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.services.UserService;

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
     * Behavior:
     * - If request validation fails, returns 400 with a map of field errors.
     * - If a user with the provided email already exists, returns 400 with an error message.
     * - On successful user creation, returns 200 with a confirmation message.
     * - On internal server error, returns 500 with an error message.
     *
     * @param userDTO Data transfer object containing user registration information (username, email, password)
     * @return ResponseEntity with a success or error message
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        if(userService.existsByEmail(userDTO.getEmail())){
            Map<String, String> errors = new HashMap<>();
            errors.put("Email", "Пользователь с таким Email уже существует");
            return ResponseEntity.badRequest().body(errors);
        }
        try{
            userService.create(userDTO);
            return ResponseEntity.ok("Пользователь зарегистрирован");
        } catch (Exception e){
            return ResponseEntity.internalServerError()
                    .body("Ошибка при регистрации пользователя");
        }

    }
}
