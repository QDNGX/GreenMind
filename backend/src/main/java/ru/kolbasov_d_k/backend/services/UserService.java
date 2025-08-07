package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.models.Role;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;

import java.security.Principal;
import java.time.LocalDate;

/**
 * Service responsible for user-related operations.
 * Provides methods for creating, finding, and updating users.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with the required dependencies.
     *
     * @param userRepository Repository for user data access
     * @param passwordEncoder Encoder for hashing passwords
     */
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
