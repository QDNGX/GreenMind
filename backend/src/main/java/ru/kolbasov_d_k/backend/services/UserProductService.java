package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.repositories.UserProductRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * Finds all products in a user's cart and converts them to a list of maps.
     *
     * @param user The user whose orders to find
     * @return A list of maps containing product information (productId, productName, quantity, imagePath, price)
     */
    public List<Map<String, Object>> findOrders(User user) {
        return userProductRepository
                .findByUser(user)
                .stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

    /**
     * Converts a UserProduct object to a map containing product information.
     *
     * @param up The UserProduct object to convert
     * @return A map containing product information (productId, productName, quantity, imagePath, price)
     */
    private Map<String, Object> convertToMap(UserProduct up) {
        Map<String, Object> m = new HashMap<>();
        m.put("productId",   up.getProduct().getId());
        m.put("productName", up.getProduct().getName());
        m.put("quantity",    up.getQuantity());
        m.put("imagePath", up.getProduct().getImagePath());
        m.put("price",    up.getProduct().getPrice());
        return m;
    }
}
