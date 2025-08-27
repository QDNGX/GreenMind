package ru.kolbasov_d_k.backend.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.ProductService;
import ru.kolbasov_d_k.backend.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;

    public AdminController(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    /**
     * REST endpoint to retrieve all users in the system.
     * This endpoint is restricted to users with ADMIN role only.
     * Returns user information in a secure format that excludes sensitive data.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.
     * The response uses UserResponseDTO to ensure no sensitive information like password hashes
     * are exposed to the client.</p>
     * 
     * <p>HTTP Method: GET</p>
     * <p>URL: /api/admin</p>
     * <p>Required Role: ADMIN</p>
     *
     * @return List of UserResponseDTO containing all users' non-sensitive information
     * @see UserResponseDTO
     */
    @GetMapping()
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}
