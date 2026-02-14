package ru.kolbasov_d_k.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.User;

import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides methods for CRUD operations on users and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user, or empty if no user is found
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email address to check
     * @return true if a user with the specified email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
