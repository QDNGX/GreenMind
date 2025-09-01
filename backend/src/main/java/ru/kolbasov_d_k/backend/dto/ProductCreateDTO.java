package ru.kolbasov_d_k.backend.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Data Transfer Object for product creation by administrators.
 * This DTO is used when administrators create new products through the admin interface.
 * 
 * <p>This DTO contains only the fields that can be specified during product creation.
 * Fields like ID and creation timestamp are automatically generated and are not included.</p>
 * 
 * <p>All validation constraints follow the application's validation patterns with
 * localized Russian error messages for consistency with other DTOs in the system.</p>
 */
public class ProductCreateDTO {

    /**
     * The name of the product.
     * Must be provided and have a reasonable length.
     */
    @NotBlank(message = "Введите название продукта")
    @Size(min = 2, max = 100, message = "Название продукта должно содержать от 2 до 100 символов")
    private String name;

    /**
     * The price of the product.
     * Must be a positive value greater than zero.
     */
    @NotNull(message = "Укажите цену продукта")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    @DecimalMax(value = "99999.99", message = "Цена не может превышать 99999.99")
    private BigDecimal price;

    /**
     * The path to the product's image file.
     * This field is optional and can be null or empty.
     */
    @Size(max = 255, message = "Путь к изображению не может превышать 255 символов")
    private String imagePath;

    /**
     * The available quantity of the product in stock.
     * Must be a non-negative integer.
     */
    @NotNull(message = "Укажите количество продукта")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    @Max(value = 10000, message = "Количество не может превышать 10000")
    private Integer quantity;

    /**
     * Default constructor.
     */
    public ProductCreateDTO() {
    }

    /**
     * Constructs a new ProductCreateDTO with the specified values.
     *
     * @param name The name of the product
     * @param price The price of the product
     * @param imagePath The path to the product's image
     * @param quantity The available quantity
     */
    public ProductCreateDTO(String name, BigDecimal price, String imagePath, Integer quantity) {
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
        this.quantity = quantity;
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