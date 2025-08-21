package ru.kolbasov_d_k.backend.controllers;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kolbasov_d_k.backend.dto.ProductDTO;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.CartService;
import ru.kolbasov_d_k.backend.services.ProductService;
import ru.kolbasov_d_k.backend.services.UserProductService;

import java.util.List;
import java.util.Map;

/**
 * Controller responsible for handling shopping cart operations.
 * Provides endpoints for viewing, adding, updating, and removing products from a user's cart.
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final UserProductService userProductService;

    /**
     * Constructs a new CartController with the required services.
     *
     * @param cartService Service for cart-related operations
     * @param productService Service for product-related operations
     * @param userProductService Service for user-product relationship operations
     */
    @Autowired
    public CartController(CartService cartService, ProductService productService, UserProductService userProductService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userProductService = userProductService;
    }

    /**
     * Retrieves all available products.
     *
     * @return List of all products in the system
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    /**
     * Adds a product to the user's cart.
     *
     * @param user The authenticated user
     * @param productDTO Data transfer object containing product ID and quantity
     * @return ResponseEntity containing the updated cart as a list of maps with product information
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException If the product or user is not found
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.OverLimitException If the requested quantity exceeds the available quantity
     */
    @PostMapping
    public ResponseEntity<List<Map<String,Object>>> add(@AuthenticationPrincipal(expression = "user") User user,
                                                        @Valid @RequestBody ProductDTO productDTO) {
        cartService.addProductToUser(
                user.getId(),
                productDTO.getId(),
                productDTO.getQuantity()
        );
        List<Map<String,Object>> cart = userProductService.findOrders(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * Updates the quantity of a product in the user's cart.
     *
     * @param user The authenticated user
     * @param productDTO Data transfer object containing product ID and the new quantity
     * @return ResponseEntity containing the updated cart as a list of maps with product information
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException If the product or user-product relationship is not found
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.OverLimitException If the requested quantity exceeds the available quantity
     */
    @PatchMapping
    public ResponseEntity<List<Map<String,Object>>> update(@AuthenticationPrincipal(expression = "user") User user,
                                                            @Valid @RequestBody ProductDTO productDTO) {
        cartService.updateProductQuantity(
                user.getId(),
                productDTO.getId(),
                productDTO.getQuantity()
        );
        List<Map<String,Object>> cart = userProductService.findOrders(user);
        return ResponseEntity.ok(cart);
    }

    /**
     * Removes a product from the user's cart.
     *
     * @param user The authenticated user
     * @param productId The ID of the product to remove
     * @return ResponseEntity containing the updated cart as a list of maps with product information
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException If the user-product relationship is not found
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<List<Map<String,Object>>> delete(@AuthenticationPrincipal(expression = "user") User user,
                                                            @PathVariable Integer productId) {
        cartService.deleteProductFromUser(
                user.getId(),
                productId
        );
        List<Map<String,Object>> cart = userProductService.findOrders(user);
        return ResponseEntity.ok(cart);
    }


}
