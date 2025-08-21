package ru.kolbasov_d_k.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object for product information used in cart operations.
 * Contains the product ID and quantity.
 */
public class ProductDTO {


    @NotNull(message = "ID продукта обязателен")
    @Positive(message = "ID продукта должен быть положительным")
    private Integer productId;

    @NotNull(message = "Количество обязательно")
    @Min(value = 1, message = "Количество не должно быть менее 1")
    private Integer quantity;

    /**
     * Default constructor.
     */
    public ProductDTO() {

    }

    /**
     * Constructs a new ProductDTO with the specified product ID and quantity.
     *
     * @param productId The ID of the product
     * @param quantity The quantity of the product
     */
    public ProductDTO(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    /**
     * Gets the quantity of the product.
     *
     * @return The quantity of the product
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product.
     *
     * @param quantity The new quantity of the product
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the ID of the product.
     *
     * @return The ID of the product
     */
    public Integer getId() {
        return productId;
    }

    /**
     * Sets the ID of the product.
     *
     * @param productId The new ID of the product
     */
    public void setId(Integer productId) {
        this.productId = productId;
    }
}
