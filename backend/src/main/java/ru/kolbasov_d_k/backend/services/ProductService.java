package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolbasov_d_k.backend.dto.ProductCreateDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for product-related operations.
 * Provides methods for retrieving, creating, and managing products with proper DTO handling.
 */
@Service
@Transactional(readOnly = true)
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
     * Retrieves all products from the database and converts them to DTOs for client response.
     * This method maintains architectural consistency by using DTOs instead of returning
     * entities directly to controllers.
     *
     * @return A list of ProductResponseDTO objects containing all products' information
     * @see ProductResponseDTO#fromEntity(Product)
     */
    public List<ProductResponseDTO> findAll() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Creates a new product from a ProductCreateDTO.
     * This method converts the DTO to a Product entity, sets the creation timestamp,
     * saves it to the database, and returns the saved product as a DTO.
     *
     * @param createDTO The ProductCreateDTO containing product information
     * @return A ProductResponseDTO representing the created product
     */
    @Transactional
    public ProductResponseDTO create(ProductCreateDTO createDTO) {
        Product product = new Product();
        product.setName(createDTO.getName());
        product.setPrice(createDTO.getPrice());
        product.setImagePath(createDTO.getImagePath());
        product.setQuantity(createDTO.getQuantity());
        product.setCreatedAt(LocalDateTime.now());
        
        Product savedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(savedProduct);
    }


    @Transactional
    public void update(Integer productId, ProductCreateDTO createDTO) {
        Product product = productRepository.findById(productId).orElseThrow();
        product.setName(createDTO.getName());
        product.setPrice(createDTO.getPrice());
        product.setImagePath(createDTO.getImagePath());
        product.setQuantity(createDTO.getQuantity());
        productRepository.save(product);
    }

    @Transactional
    public void delete(Integer productId) {
        productRepository.deleteById(productId);
    }
}
