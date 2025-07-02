package ru.kolbasov_d_k.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
