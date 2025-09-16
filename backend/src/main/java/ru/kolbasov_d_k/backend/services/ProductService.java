package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolbasov_d_k.backend.dto.ProductCreateDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;

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


    /**
     * Retrieves a single product by its ID.
     * This method is used for getting detailed information about a specific product.
     *
     * @param productId The ID of the product to retrieve
     * @return A ProductResponseDTO representing the product
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if product not found
     */
    public ProductResponseDTO findById(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));
        return ProductResponseDTO.fromEntity(product);
    }

    /**
     * Updates an existing product with new information.
     * This method updates all fields of the product based on the provided DTO.
     *
     * @param productId The ID of the product to update
     * @param createDTO The ProductCreateDTO containing updated product information
     * @return A ProductResponseDTO representing the updated product
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if product not found
     */
    @Transactional
    public ProductResponseDTO update(Integer productId, ProductCreateDTO createDTO) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));
        product.setName(createDTO.getName());
        product.setPrice(createDTO.getPrice());
        product.setImagePath(createDTO.getImagePath());
        product.setQuantity(createDTO.getQuantity());
        Product updatedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     * This method permanently removes the product from the database.
     *
     * @param productId The ID of the product to delete
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if product not found
     */
    @Transactional
    public void delete(Integer productId) {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Product", productId);
        }
        productRepository.deleteById(productId);
    }
}
