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

@Service
public class CartService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final UserProductRepository userProductRepository;

    @Autowired
    public CartService(ProductRepository productRepository, UserRepository userRepository, UserProductRepository userProductRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.userProductRepository = userProductRepository;
    }

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

    @Transactional
    public void updateProductQuantity(Integer userId, Integer productId, int quantity) {
        Product product = productRepository
                .findByIdForUpdate(productId)
                .orElseThrow(() -> new NotFoundException("Product", productId));

        UserProductId id = new UserProductId(userId, product.getId());
        UserProduct userProduct = userProductRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("UserProduct", id));


        int diff = quantity - userProduct.getQuantity();
        if(diff > 0 && product.getQuantity() < quantity) {
            throw new OverLimitException("Product", quantity, product.getQuantity());
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
