package ru.kolbasov_d_k.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.models.UserProductId;

import java.util.List;

/**
 * Repository interface for UserProduct entity operations.
 * Provides methods for CRUD operations on user-product relationships (cart items)
 * and custom query methods.
 */
@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, UserProductId> {

    /**
     * Finds all products in a user's cart.
     *
     * @param user The user whose cart items to find
     * @return A list of UserProduct entities representing the user's cart items
     */
    List<UserProduct> findByUser(User user);
}
