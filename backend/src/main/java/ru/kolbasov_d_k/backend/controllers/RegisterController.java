package ru.kolbasov_d_k.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.services.UserService;

@RestController
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO) {
        userService.create(userDTO);
        return ResponseEntity.ok("Пользователь зарегистрирован");
    }
}
