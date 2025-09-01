package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.models.UserProductId;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;
import ru.kolbasov_d_k.backend.repositories.UserProductRepository;
import ru.kolbasov_d_k.backend.repositories.UserRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;
import ru.kolbasov_d_k.backend.utils.exceptions.OverLimitException;

/**
 * Service responsible for shopping cart operations.
 * Provides methods for adding, updating, and removing products from a user's cart.
 */
@Service
public class CartService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductRepository userProductRepository;

    /**
     * Constructs a new CartService with the required dependencies.
     *
     * @param productRepository Repository for product data access
     * @param userRepository Repository for user data access
     * @param userProductRepository Repository for user-product relationship data access
     */
    @Autowired
    public CartService(ProductRepository productRepository, UserRepository userRepository, UserProductRepository userProductRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
    }

    /**
     * Adds a product to a user's cart.
     * Decreases the available quantity of the product and increases the quantity in the user's cart.
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to add
     * @param quantity The quantity of the product to add
     * @throws NotFoundException If the product or user is not found
     * @throws OverLimitException If the requested quantity exceeds the available quantity
     */
    @Transactional
    public void addProductToUser(Integer userId, Integer productId, int quantity) {
        Product product = productRepository
                .findByIdForUpdate(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        if(product.getQuantity() < quantity) {
            throw new OverLimitException("Product", quantity, product.getQuantity());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));

        product.setQuantity(product.getQuantity() - quantity);

        UserProductId id = new UserProductId(user.getId(), product.getId());
        UserProduct userProduct = userProductRepository.findById(id)
                .orElse(new UserProduct(id,user,product,0));

        userProduct.setQuantity(userProduct.getQuantity() + quantity);
        userProductRepository.save(userProduct);
        productRepository.save(product);
    }

    /**
     * Updates the quantity of a product in a user's cart.
     * Adjusts the available quantity of the product based on the difference between the new and old quantities.
     * If the new quantity is 0 or negative, the product is removed from the cart.
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to update
     * @param quantity The new quantity of the product
     * @throws NotFoundException If the product or user-product relationship is not found
     * @throws OverLimitException If the requested quantity increase exceeds the available quantity
     */
    @Transactional
    public void updateProductQuantity(Integer userId, Integer productId, int quantity) {
        Product product = productRepository
                .findByIdForUpdate(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        UserProductId id = new UserProductId(userId, product.getId());
        UserProduct userProduct = userProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserProduct", id));


        int diff = quantity - userProduct.getQuantity();
        if(diff > 0 && product.getQuantity() < diff) {
            throw new OverLimitException("Product", diff, product.getQuantity());
        }

        product.setQuantity(product.getQuantity() - diff);

        if(quantity <= 0){
            userProductRepository.delete(userProduct);
        }
        else{
            userProduct.setQuantity(quantity);
            userProductRepository.save(userProduct);
        }

        productRepository.save(product);
    }

    /**
     * Removes a product from a user's cart.
     * Returns the quantity of the product back to the available quantity.
     *
     * @param userId The ID of the user
     * @param productId The ID of the product to remove
     * @throws NotFoundException If the user-product relationship is not found
     */
    @Transactional
    public void deleteProductFromUser(Integer userId, Integer productId) {
        UserProductId id = new UserProductId(userId, productId);
        UserProduct userProduct = userProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserProduct", id));

        Product product = userProduct.getProduct();
        product.setQuantity(product.getQuantity() + userProduct.getQuantity());

        userProductRepository.delete(userProduct);
        productRepository.save(product);
    }
}
