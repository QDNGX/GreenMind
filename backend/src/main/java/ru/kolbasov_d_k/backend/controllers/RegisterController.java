package ru.kolbasov_d_k.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.services.UserService;

/**
 * Controller responsible for handling user registration.
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
     *
     * @param userDTO Data transfer object containing user registration information (username, email, password)
     * @return ResponseEntity with a success message if registration is successful
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        userService.create(userDTO);
        return ResponseEntity.ok("Пользователь зарегистрирован");
    }
}
