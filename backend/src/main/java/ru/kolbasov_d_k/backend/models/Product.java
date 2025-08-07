package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a product in the system.
 * This class is mapped to the "products" table in the database.
 * It includes product details such as name, price, image path, and available quantity.
 */
@Entity
@Table(name = "products")
public class Product {

    /**
     * The unique identifier for the product.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * The name of the product.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The price of the product.
     * Stored with precision of 10 digits and 2 decimal places.
     */
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    /**
     * The path to the product's image file.
     */
    @Column(name = "image_path")
    private String imagePath;

    /**
     * The timestamp when the product was created.
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * The available quantity of the product in stock.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Default constructor.
     */
    public Product() {
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
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
