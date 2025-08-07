package ru.kolbasov_d_k.backend.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.Product;

import java.util.Optional;

/**
 * Repository interface for Product entity operations.
 * Provides methods for CRUD operations on products and custom query methods.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Finds a product by its ID with a pessimistic write lock.
     * This method is used for operations that require exclusive access to the product,
     * such as updating the product's quantity during cart operations.
     * The pessimistic lock prevents concurrent modifications to the same product.
     *
     * @param id The ID of the product to find
     * @return An Optional containing the product if found, or an empty Optional if not found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") int id);
}
