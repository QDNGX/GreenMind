package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kolbasov_d_k.backend.dto.CartItemResponseDTO;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserProductRepository;

import java.util.List;

/**
 * Service responsible for user-product relationship operations.
 * Provides methods for retrieving and formatting user orders.
 */
@Service
public class UserProductService {

    private final UserProductRepository userProductRepository;

    /**
     * Constructs a new UserProductService with the required dependency.
     *
     * @param userProductRepository Repository for user-product relationship data access
     */
    @Autowired
    public UserProductService(UserProductRepository userProductRepository) {
        this.userProductRepository = userProductRepository;
    }

    /**
     * Finds all products in a user's cart and converts them to a list of CartItemResponseDTO.
     * Uses JOIN FETCH to avoid N+1 query problem.
     *
     * @param user The user whose orders to find
     * @return A list of CartItemResponseDTO containing product information
     */
    public List<CartItemResponseDTO> findOrders(User user) {
        return userProductRepository
                .findByUserWithProduct(user)
                .stream()
                .map(CartItemResponseDTO::fromEntity)
                .toList();
    }
}
