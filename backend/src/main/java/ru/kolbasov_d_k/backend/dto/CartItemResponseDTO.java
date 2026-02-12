package ru.kolbasov_d_k.backend.dto;

import ru.kolbasov_d_k.backend.models.UserProduct;

import java.math.BigDecimal;

public class CartItemResponseDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private String imagePath;
    private BigDecimal price;

    public static CartItemResponseDTO fromEntity(UserProduct up){
        CartItemResponseDTO dto = new CartItemResponseDTO();

        dto.setProductId(up.getProduct().getId());
        dto.setProductName(up.getProduct().getName());
        dto.setQuantity(up.getQuantity());
        dto.setImagePath(up.getProduct().getImagePath());
        dto.setPrice(up.getProduct().getPrice());

        return dto;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
