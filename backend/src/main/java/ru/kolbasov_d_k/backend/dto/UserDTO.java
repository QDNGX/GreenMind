package ru.kolbasov_d_k.backend.dto;

/**
 * Data Transfer Object for user information used in registration and user operations.
 * Contains the username, email, and password for a user.
 */
public class UserDTO {

    private String username;
    private String email;
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
