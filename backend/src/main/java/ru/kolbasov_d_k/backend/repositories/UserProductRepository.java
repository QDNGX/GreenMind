package ru.kolbasov_d_k.backend.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.models.UserProductId;

import java.util.List;

@Repository
public interface UserProductRepository extends JpaRepository<UserProduct, UserProductId> {

    List<UserProduct> findByUser(User user);
}
