package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Embeddable class representing a composite primary key for the UserProduct entity.
 * This class combines a user ID and a product ID to form a unique identifier for a cart item.
 * It implements equals and hashCode methods to ensure proper comparison and hashing.
 */
@Embeddable
public class UserProductId {

    /**
     * The ID of the user who has the product in their cart.
     */
    private int userId;
    
    /**
     * The ID of the product in the user's cart.
     */
    private int productId;

    /**
     * Default constructor required by JPA.
     */
    public UserProductId() {}

    /**
     * Constructs a new UserProductId with the specified user ID and product ID.
     *
     * @param userId The ID of the user
     * @param productId The ID of the product
     */
    public UserProductId(int userId, int productId) {
        this.userId = userId;
        this.productId = productId;
    }

    /**
     * Compares this UserProductId with another object for equality.
     * Two UserProductId objects are considered equal if they have the same user ID and product ID.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProductId that = (UserProductId) o;
        return userId == that.userId && productId == that.productId;
    }

    /**
     * Generates a hash code for this UserProductId.
     * The hash code is based on both the user ID and product ID.
     *
     * @return The hash code for this UserProductId
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }

    /**
     * Gets the user ID component of this composite key.
     *
     * @return The user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID component of this composite key.
     *
     * @param userId The new user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the product ID component of this composite key.
     *
     * @return The product ID
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Sets the product ID component of this composite key.
     *
     * @param productId The new product ID
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }
}
