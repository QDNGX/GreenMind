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

@Service
public class UserProductService {

    private final UserProductRepository userProductRepository;

    @Autowired
    public UserProductService(UserProductRepository userProductRepository) {
        this.userProductRepository = userProductRepository;
    }

    public List<Map<String, Object>> findOrders(User user) {
        return userProductRepository
                .findByUser(user)
                .stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
    }

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
