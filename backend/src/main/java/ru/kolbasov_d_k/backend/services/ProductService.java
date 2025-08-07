package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;

import java.util.List;

/**
 * Service responsible for product-related operations.
 * Provides methods for retrieving products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Constructs a new ProductService with the required dependency.
     *
     * @param productRepository Repository for product data access
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Retrieves all products from the database.
     *
     * @return A list of all products
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}
