package ru.kolbasov_d_k.backend.dto;

import ru.kolbasov_d_k.backend.models.Role;
import ru.kolbasov_d_k.backend.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for user information returned to clients.
 * This DTO is used to transfer user data in API responses without exposing sensitive information.
 * 
 * <p>Security consideration: This DTO deliberately excludes the passwordHash field from the User entity
 * to prevent accidental exposure of password hashes in API responses.</p>
 * 
 * <p>This DTO includes all user profile information that is safe to expose to clients,
 * including user ID, username, email, birth date, timestamps, and role information.</p>
 */
public class UserResponseDTO {
    
    /**
     * The unique identifier of the user.
     */
    private Integer id;
    
    /**
     * The display name of the user.
     */
    private String username;
    
    /**
     * The email address of the user.
     */
    private String email;
    
    /**
     * The birth date of the user.
     */
    private LocalDate birthDate;
    
    /**
     * The timestamp when the user was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * The timestamp when the user was last updated.
     */
    private LocalDateTime updatedAt;
    
    /**
     * The role of the user (USER or ADMIN).
     */
    private Role role;

    /**
     * Creates a UserResponseDTO from a User entity.
     * This static factory method performs secure mapping by copying only non-sensitive fields
     * from the User entity to the DTO.
     *
     * <p>Security note: The passwordHash field is intentionally excluded from the mapping
     * to prevent sensitive information from being exposed in API responses.</p>
     *
     * @param user The User entity to convert to DTO. Must not be null.
     * @return A new UserResponseDTO instance containing the user's non-sensitive information
     * @throws NullPointerException if the user parameter is null and any of its methods are called
     */
    public static UserResponseDTO fromEntity(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRole(user.getRole());
        return dto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
