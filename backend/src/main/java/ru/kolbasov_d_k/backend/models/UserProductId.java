package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class UserProductId {

    private int userId;
    private int productId;

    public UserProductId() {}

    public UserProductId(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProductId that = (UserProductId) o;
        return userId == that.userId && productId == that.productId;
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
