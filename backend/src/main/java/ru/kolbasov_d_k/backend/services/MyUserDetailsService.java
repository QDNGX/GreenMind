package ru.kolbasov_d_k.backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;

import java.util.Collection;
import java.util.List;

/**
 * Service responsible for loading user details for authentication.
 * Implements Spring Security's UserDetailsService interface.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Constructs a new MyUserDetailsService with the required dependency.
     *
     * @param userRepository Repository for user data access
     */
    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their email address for authentication.
     * The email is used as the username in this application.
     *
     * @param email The email address of the user to load
     * @return UserDetails object containing the user's authentication information
     * @throws UsernameNotFoundException if the user with the specified email is not found
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Не найден:  " + email);
        }

        return new SecurityUserDetails(user);
    }

    /**
     * Record class that adapts our User model to Spring Security's UserDetails interface.
     * This allows our User model to be used for authentication and authorization.
     */
    private record SecurityUserDetails(User user) implements UserDetails {

        /**
         * Returns the authorities granted to the user.
         * In this application, the authority is based on the user's role.
         *
         * @return A collection of granted authorities
         */
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        }

        /**
         * Returns the password used to authenticate the user.
         *
         * @return The user's password hash
         */
        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        /**
         * Returns the username used to authenticate the user.
         * In this application, the email is used as the username.
         *
         * @return The user's email
         */
        @Override
        public String getUsername() {
            return user.getEmail();
        }

        /**
         * Indicates whether the user's account has expired.
         *
         * @return true if the user's account is valid (non-expired), false otherwise
         */
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
         * Indicates whether the user is locked or unlocked.
         *
         * @return true if the user is not locked, false otherwise
         */
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
         * Indicates whether the user's credentials (password) has expired.
         *
         * @return true if the user's credentials are valid (non-expired), false otherwise
         */
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        /**
         * Indicates whether the user is enabled or disabled.
         *
         * @return true if the user is enabled, false otherwise
         */
        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
