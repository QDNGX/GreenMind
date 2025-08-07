package ru.kolbasov_d_k.backend.models;


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a user in the system.
 * This class is mapped to the "users" table in the database.
 * It includes user authentication information, profile details, and auditing fields.
 */
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
public class User {

    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
     * The display name of the user.
     */
    @Column(name = "name", nullable = false)
    private String username;

    /**
     * The hashed password of the user.
     */
    @Column(name = "password_hash")
    private String passwordHash;

    /**
     * The email address of the user, used for authentication.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * The birth date of the user.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /**
     * The timestamp when the user was created.
     * This field is automatically set and cannot be updated.
     */
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * The timestamp when the user was last updated.
     * This field is automatically updated on each save.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * The role of the user in the system (e.g., USER, ADMIN).
     */
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Default constructor.
     */
    public User() {
    }

    /**
     * Gets the ID of the user.
     *
     * @return The ID of the user
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the user.
     *
     * @param id The new ID of the user
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the username of the user.
     *
     * @return The username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     *
     * @param username The new username of the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password hash of the user.
     *
     * @return The password hash of the user
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash of the user.
     *
     * @param passwordHash The new password hash of the user
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Gets the email address of the user.
     *
     * @return The email address of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email The new email address of the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the birth date of the user.
     *
     * @return The birth date of the user
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of the user.
     *
     * @param birthDate The new birth date of the user
     */
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Gets the creation timestamp of the user.
     *
     * @return The creation timestamp of the user
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the user.
     *
     * @param createdAt The new creation timestamp of the user
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp of the user.
     *
     * @return The last update timestamp of the user
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp of the user.
     *
     * @param updatedAt The new last update timestamp of the user
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the role of the user.
     *
     * @return The role of the user
     */
    public Role getRole() {
        return role;
    }

    /**
     * Sets the role of the user.
     *
     * @param role The new role of the user
     */
    public void setRole(Role role) {
        this.role = role;
    }
}
