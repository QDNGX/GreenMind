package ru.kolbasov_d_k.backend.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

}
