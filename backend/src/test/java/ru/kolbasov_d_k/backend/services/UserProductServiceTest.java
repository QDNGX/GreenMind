package ru.kolbasov_d_k.backend.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kolbasov_d_k.backend.dto.CartItemResponseDTO;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.repositories.UserProductRepository;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName( "UserProductService Test - Unit Tests")
public class UserProductServiceTest {

    @Mock
    private UserProductRepository userProductRepository;

    @InjectMocks
    private UserProductService userProductService;

    private User testUser;
    private UserProduct testUserProduct;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("TestUser@Test");

        testUserProduct = new UserProduct();
        testUserProduct.setProduct(new Product());
        testUserProduct.setUser(new User());
    }

    @Nested
    @DisplayName("findOrders()")
    class FindOrders{
        @Test
        @DisplayName("Return empty list if no orders exist")
        void findOrders_noOrdersExist_returnEmptyList(){
            //Arrange
            when(userProductRepository.findByUserWithProduct(testUser)).thenReturn(Collections.emptyList());

            //Act
            List<CartItemResponseDTO> result = userProductService.findOrders(testUser);

            //Assert
            assertThat(result).hasSize(0);
        }
        @Test
        @DisplayName("Return list of dtos if orders exist")
        void findOrders_ordersExist_returnListOfDTOs(){
            //Arrange
            UserProduct testUserProduct2 = new UserProduct();
            testUserProduct2.setProduct(new Product());
            testUserProduct2.setUser(new User());
            when(userProductRepository.findByUserWithProduct(testUser)).thenReturn(List.of(testUserProduct,testUserProduct2));

            //Act
            List<CartItemResponseDTO> result = userProductService.findOrders(testUser);

            //Assert
            assertThat(result).hasSize(2);
        }
    }
}
