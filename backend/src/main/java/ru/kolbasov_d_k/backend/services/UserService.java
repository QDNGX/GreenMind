package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
import ru.kolbasov_d_k.backend.models.Role;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for user-related operations.
 * Provides methods for creating, finding, and updating users.
 */
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProductService userProductService;

    /**
     * Constructs a new UserService with the required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for hashing passwords
     * @param userProductService Service for user-product operations
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserProductService userProductService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProductService = userProductService;
    }

    /**
     * Finds a user by their ID.
     *
     * @param id The ID of the user to find
     * @return The user with the specified ID, or null if no user is found
     */
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user to find
     * @return The user with the specified email address, or null if no user is found
     */
    private User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email The email address to check
     * @return true if a user with the specified email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Gets the current user from the Principal object.
     *
     * @param principal The Principal object representing the current user
     * @return The User object for the current user
     * @throws IllegalArgumentException if the principal is null (user is not logged in)
     */
    public User getCurrentUser(Principal principal) {
        if(principal == null) {
            throw new IllegalArgumentException("User is not logged in");
        }
        return findByEmail(principal.getName());
    }

    /**
     * Retrieves all users from the database and converts them to DTOs for safe client response.
     * This method performs secure data transfer by mapping User entities to UserResponseDTO objects,
     * which exclude sensitive information like password hashes.
     * 
     * <p>The method uses Java Stream API with method reference for efficient collection processing,
     * converting each User entity to a UserResponseDTO using the static factory method.</p>
     * 
     * <p>Security consideration: This method ensures that sensitive user information 
     * (such as password hashes) is never exposed in the response.</p>
     *
     * @return A list of UserResponseDTO objects containing all users' non-sensitive information
     * @see UserResponseDTO#fromEntity(User)
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Creates a new user from a UserDTO.
     * The user is assigned the USER role and their password is hashed.
     *
     * @param userDTO Data transfer object containing user information (username, email, password)
     */
    @Transactional
    public void create(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    /**
     * Updates a user's username.
     *
     * @param user The user to update
     * @param username The new username
     */
    @Transactional
    public void updateUserName(User user, String username) {
        user.setUsername(username);
        userRepository.save(user);
    }

    /**
     * Updates a user's email address.
     *
     * @param user The user to update
     * @param email The new email address
     */
    @Transactional
    public void updateUserEmail(User user, String email) {
        user.setEmail(email);
        userRepository.save(user);
    }

    /**
     * Updates a user's birth date.
     *
     * @param user The user to update
     * @param birthDate The new birth date
     */
    @Transactional
    public void updateUserBirthDate(User user, LocalDate birthDate) {
        user.setBirthDate(birthDate);
        userRepository.save(user);
    }

    /**
     * Deletes a user by their ID.
     * This method is used by administrators to remove users from the system.
     * 
     * @param userId The ID of the user to delete
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if user not found
     */
    @Transactional
    public void deleteUser(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User", userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Updates the role of a user.
     * This method is used by administrators to promote/demote users between USER and ADMIN roles.
     * 
     * @param userId The ID of the user whose role to update
     * @param newRole The new role to assign to the user (USER or ADMIN)
     * @return UserResponseDTO with updated user information
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if user not found
     * @throws IllegalArgumentException if role is invalid
     */
    @Transactional
    public UserResponseDTO updateUserRole(Integer userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        
        // Validate and convert role string to enum
        try {
            ru.kolbasov_d_k.backend.models.Role role = ru.kolbasov_d_k.backend.models.Role.valueOf(newRole);
            user.setRole(role);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Must be USER or ADMIN");
        }
        
        User updatedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(updatedUser);
    }

    /**
     * Retrieves all orders for a specific user.
     * This method is used by administrators to view user purchase history.
     * Delegates to UserProductService to retrieve user orders.
     * 
     * @param userId The ID of the user whose orders to retrieve
     * @return List of orders (products in cart) for the user as Map objects
     * @throws ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException if user not found
     */
    public List<java.util.Map<String, Object>> getUserOrders(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User", userId));
        
        // Delegate to UserProductService which has the necessary repository access
        return userProductService.findOrders(user);
    }
}
