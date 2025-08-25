package ru.kolbasov_d_k.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for user information used in registration and user operations.
 * Contains the username, email, and password for a user.
 */
public class UserDTO {


    @NotBlank(message = "Введите имя")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String username;

    @NotBlank(message = "Введите email")
    @Email(message = "Некорректный формат Email")
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 8, message = "Пароль должен содержать минимум 8 символов")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
            message = "Пароль должен содержать цифры, заглавные и строчные буквы")
    private String password;

    /**
     * Default constructor.
     */
    public UserDTO() {
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
     * Gets the password of the user.
     *
     * @return The password of the user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of the user.
     *
     * @param password The new password of the user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
