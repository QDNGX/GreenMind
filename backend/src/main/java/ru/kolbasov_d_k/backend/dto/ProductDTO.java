package ru.kolbasov_d_k.backend.dto;

public class ProductDTO {

    private Integer productId;
    private Integer quantity;

    public ProductDTO() {

    }
    public ProductDTO(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getId() {
        return productId;
    }

    public void setId(Integer productId) {
        this.productId = productId;
    }
}
