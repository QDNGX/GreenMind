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


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getCurrentUser(Principal principal) {
        if(principal == null) {
            throw new IllegalArgumentException("User is not logged in");
        }
        return findByEmail(principal.getName());
    }

    @Transactional
    public void create(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserName(User user, String username) {
        user.setUsername(username);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserEmail(User user, String email) {
        user.setEmail(email);
        userRepository.save(user);
    }

    @Transactional
    public void updateUserBirthDate(User user, LocalDate birthDate) {
        user.setBirthDate(birthDate);
        userRepository.save(user);
    }
}
