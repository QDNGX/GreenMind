package ru.kolbasov_d_k.backend.dto;

import ru.kolbasov_d_k.backend.models.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for product information returned to clients.
 * This DTO is used to transfer product data in API responses following architectural consistency.
 * 
 * <p>While Product entity doesn't contain sensitive information like User entity,
 * using a separate response DTO maintains architectural consistency and allows for
 * future flexibility in controlling what product information is exposed to different clients.</p>
 * 
 * <p>This DTO includes all product information that clients need to display products,
 * including ID, name, price, image path, creation timestamp, and available quantity.</p>
 */
public class ProductResponseDTO {
    
    /**
     * The unique identifier of the product.
     */
    private Integer id;
    
    /**
     * The name of the product.
     */
    private String name;
    
    /**
     * The price of the product.
     */
    private BigDecimal price;
    
    /**
     * The path to the product's image file.
     */
    private String imagePath;
    
    /**
     * The timestamp when the product was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * The available quantity of the product in stock.
     */
    private Integer quantity;

    /**
     * Creates a ProductResponseDTO from a Product entity.
     * This static factory method performs mapping by copying all relevant fields
     * from the Product entity to the DTO.
     *
     * <p>This method ensures consistent data transformation and can be easily
     * modified in the future if certain product information needs to be filtered
     * or transformed before being sent to clients.</p>
     *
     * @param product The Product entity to convert to DTO. Must not be null.
     * @return A new ProductResponseDTO instance containing the product's information
     * @throws NullPointerException if the product parameter is null and any of its methods are called
     */
    public static ProductResponseDTO fromEntity(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setImagePath(product.getImagePath());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setQuantity(product.getQuantity());
        return dto;
    }

    /**
     * Gets the ID of the product.
     *
     * @return The ID of the product
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the product.
     *
     * @param id The new ID of the product
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the name of the product.
     *
     * @return The name of the product
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the product.
     *
     * @param name The new name of the product
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the price of the product.
     *
     * @return The price of the product
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the price of the product.
     *
     * @param price The new price of the product
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * Gets the image path of the product.
     *
     * @return The image path of the product
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     * Sets the image path of the product.
     *
     * @param imagePath The new image path of the product
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * Gets the creation timestamp of the product.
     *
     * @return The creation timestamp of the product
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the product.
     *
     * @param createdAt The new creation timestamp of the product
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the available quantity of the product.
     *
     * @return The available quantity of the product
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the available quantity of the product.
     *
     * @param quantity The new available quantity of the product
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}