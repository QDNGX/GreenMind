package ru.kolbasov_d_k.backend.controllers;


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


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    UserProductService userProductService;

    @Autowired
    public CartController(CartService cartService, ProductService productService, UserProductService userProductService) {
        this.cartService = cartService;
        this.productService = productService;
        this.userProductService = userProductService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    @PostMapping
    public ResponseEntity<List<Map<String,Object>>> add(@AuthenticationPrincipal(expression = "user") User user,
                                                        @RequestBody ProductDTO productDTO) {
        cartService.addProductToUser(
                user.getId(),
                productDTO.getId(),
                productDTO.getQuantity()
        );
        List<Map<String,Object>> cart = userProductService.findOrders(user);
        return ResponseEntity.ok(cart);
    }
}
