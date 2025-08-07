package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.*;

/**
 * Entity class representing the relationship between a user and a product (cart item).
 * This class is mapped to the "user_products" table in the database.
 * It implements a many-to-many relationship between users and products with additional attributes.
 */
@Entity
@Table(name = "user_products")
public class UserProduct {

    /**
     * The composite primary key for this entity, consisting of user ID and product ID.
     */
    @EmbeddedId
    private UserProductId id;

    /**
     * The user who has this product in their cart.
     * This field is lazily loaded and mapped to the userId part of the composite key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The product in the user's cart.
     * This field is lazily loaded and mapped to the productId part of the composite key.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The quantity of the product in the user's cart.
     * Defaults to 1 if not specified.
     */
    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    /**
     * Default constructor.
     */
    public UserProduct(){}

    /**
     * Constructs a new UserProduct with the specified ID, user, product, and quantity.
     * The ID is recreated using the user and product IDs to ensure consistency.
     *
     * @param id The composite ID for this user-product relationship
     * @param user The user who has this product in their cart
     * @param product The product in the user's cart
     * @param quantity The quantity of the product in the user's cart
     */
    public UserProduct(UserProductId id, User user, Product product, int quantity) {
        this.id = new UserProductId(
                user.getId(), product.getId()
        );
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Gets the composite ID of this user-product relationship.
     *
     * @return The composite ID of this user-product relationship
     */
    public UserProductId getId() {
        return id;
    }

    /**
     * Sets the composite ID of this user-product relationship.
     *
     * @param id The new composite ID of this user-product relationship
     */
    public void setId(UserProductId id) {
        this.id = id;
    }

    /**
     * Gets the user who has this product in their cart.
     *
     * @return The user who has this product in their cart
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user who has this product in their cart.
     *
     * @param user The new user who has this product in their cart
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the product in the user's cart.
     *
     * @return The product in the user's cart
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product in the user's cart.
     *
     * @param product The new product in the user's cart
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the quantity of the product in the user's cart.
     *
     * @return The quantity of the product in the user's cart
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product in the user's cart.
     *
     * @param quantity The new quantity of the product in the user's cart
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
