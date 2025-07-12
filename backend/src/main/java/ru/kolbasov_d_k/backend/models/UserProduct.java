package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.*;

@Entity
@Table(name = "user_products")
public class UserProduct {

    @EmbeddedId
    private UserProductId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    public UserProduct(){}

    public UserProduct(UserProductId id, User user, Product product, int quantity) {
        this.id = new UserProductId(
                user.getId(), product.getId()
        );
        this.user = user;
        this.product = product;
        this.quantity = quantity;
    }

    public UserProductId getId() {
        return id;
    }

    public void setId(UserProductId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
