package ru.kolbasov_d_k.backend.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.ProductCreateDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
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

    /**
     * REST endpoint to create a new product.
     * This endpoint is restricted to users with ADMIN role only.
     * Accepts product information via JSON request body and creates a new product.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.
     * Input validation is performed using Bean Validation annotations on ProductCreateDTO.</p>
     * 
     * <p>HTTP Method: POST</p>
     * <p>URL: /api/admin/createProduct</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param createDTO The ProductCreateDTO containing product information for creation
     * @return ResponseEntity with the created ProductResponseDTO and HTTP 201 status
     * @see ProductCreateDTO
     * @see ProductResponseDTO
     */
    @PostMapping("/createProduct")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductResponseDTO createdProduct = productService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
}
