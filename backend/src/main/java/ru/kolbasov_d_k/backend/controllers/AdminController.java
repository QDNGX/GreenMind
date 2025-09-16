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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * <p>URL: /api/admin/users</p>
     * <p>Required Role: ADMIN</p>
     *
     * @return List of UserResponseDTO containing all users' non-sensitive information
     * @see UserResponseDTO
     */
    @GetMapping("/users")
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * REST endpoint to retrieve all products in the system.
     * This endpoint is restricted to users with ADMIN role only.
     * 
     * <p>HTTP Method: GET</p>
     * <p>URL: /api/admin/products</p>
     * <p>Required Role: ADMIN</p>
     *
     * @return List of ProductResponseDTO containing all products' information
     * @see ProductResponseDTO
     */
    @GetMapping("/products")
    public List<ProductResponseDTO> getAllProducts() {
        return productService.findAll();
    }

    /**
     * REST endpoint to retrieve a specific product by its ID.
     * This endpoint is restricted to users with ADMIN role only.
     * 
     * <p>HTTP Method: GET</p>
     * <p>URL: /api/admin/products/{id}</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param id The ID of the product to retrieve
     * @return ProductResponseDTO containing the product's information
     * @see ProductResponseDTO
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Integer id) {
        ProductResponseDTO product = productService.findById(id);
        return ResponseEntity.ok(product);
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
     * <p>URL: /api/admin/products</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param createDTO The ProductCreateDTO containing product information for creation
     * @return ResponseEntity with the created ProductResponseDTO and HTTP 201 status
     * @see ProductCreateDTO
     * @see ProductResponseDTO
     */
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO createDTO) {
        ProductResponseDTO createdProduct = productService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    /**
     * REST endpoint to update an existing product.
     * This endpoint is restricted to users with ADMIN role only.
     * Accepts updated product information via JSON request body.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.
     * Input validation is performed using Bean Validation annotations on ProductCreateDTO.</p>
     * 
     * <p>HTTP Method: PUT</p>
     * <p>URL: /api/admin/products/{id}</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param id The ID of the product to update
     * @param updateDTO The ProductCreateDTO containing updated product information
     * @return ResponseEntity with the updated ProductResponseDTO and HTTP 200 status
     * @see ProductCreateDTO
     * @see ProductResponseDTO
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Integer id, 
                                                           @Valid @RequestBody ProductCreateDTO updateDTO) {
        ProductResponseDTO updatedProduct = productService.update(id, updateDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * REST endpoint to delete a product.
     * This endpoint is restricted to users with ADMIN role only.
     * Permanently removes the product from the database.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.</p>
     * 
     * <p>HTTP Method: DELETE</p>
     * <p>URL: /api/admin/products/{id}</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param id The ID of the product to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * REST endpoint to delete a user.
     * This endpoint is restricted to users with ADMIN role only.
     * Permanently removes the user from the database.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.</p>
     * 
     * <p>HTTP Method: DELETE</p>
     * <p>URL: /api/admin/users/{id}</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param id The ID of the user to delete
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * REST endpoint to update a user's role.
     * This endpoint is restricted to users with ADMIN role only.
     * Updates the user's role to either USER or ADMIN.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.</p>
     * 
     * <p>HTTP Method: PATCH</p>
     * <p>URL: /api/admin/users/{id}/role</p>
     * <p>Required Role: ADMIN</p>
     * <p>Request Body: {"role": "USER" | "ADMIN"}</p>
     *
     * @param id The ID of the user whose role to update
     * @param requestBody Map containing the new role value
     * @return ResponseEntity with the updated UserResponseDTO and HTTP 200 status
     */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponseDTO> updateUserRole(@PathVariable Integer id, 
                                                          @RequestBody Map<String, String> requestBody) {
        String newRole = requestBody.get("role");
        if (newRole == null || newRole.isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Role is required");
            return ResponseEntity.badRequest().body(null);
        }
        
        UserResponseDTO updatedUser = userService.updateUserRole(id, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * REST endpoint to view a user's orders.
     * This endpoint is restricted to users with ADMIN role only.
     * Returns all products in the user's cart/order history.
     * 
     * <p>Security: This endpoint is protected by Spring Security and requires ADMIN role.</p>
     * 
     * <p>HTTP Method: GET</p>
     * <p>URL: /api/admin/users/{id}/orders</p>
     * <p>Required Role: ADMIN</p>
     *
     * @param id The ID of the user whose orders to retrieve
     * @return ResponseEntity with list of Map objects containing user's orders
     */
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<List<Map<String, Object>>> getUserOrders(@PathVariable Integer id) {
        List<Map<String, Object>> orders = userService.getUserOrders(id);
        return ResponseEntity.ok(orders);
    }
}
