package ru.kolbasov_d_k.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
