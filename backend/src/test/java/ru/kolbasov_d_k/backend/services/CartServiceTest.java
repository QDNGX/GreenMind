package ru.kolbasov_d_k.backend.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.models.UserProduct;
import ru.kolbasov_d_k.backend.models.UserProductId;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;
import ru.kolbasov_d_k.backend.repositories.UserProductRepository;
import ru.kolbasov_d_k.backend.repositories.UserRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;
import ru.kolbasov_d_k.backend.utils.exceptions.OverLimitException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Test - Unit Tests")
public class CartServiceTest {


    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserProductRepository userProductRepository;

    @InjectMocks
    private CartService cartService;

    Product testProduct;
    User testUser;

    @BeforeEach
    void setUp(){
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setQuantity(100);
        testProduct.setImagePath("https://example.com/image.jpg");
        testProduct.setPrice(new java.math.BigDecimal("10.00"));

        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("TestUser");
        testUser.setEmail("TestUser@Test");
    }

    @Nested
    @DisplayName("addProductToUser()")
    class AddProductToUser{

        @Test
        @DisplayName("Add product to user if all remaining items are exist")
        void addProductToUser_allItemsExist_addsToCart(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.empty());

            //Act
            cartService.addProductToUser(1,1, 2);

            //Assert
            ArgumentCaptor<UserProduct> userProductArgumentCaptor = ArgumentCaptor.forClass(UserProduct.class);
            verify(userProductRepository).save(userProductArgumentCaptor.capture());
            verify(productRepository).save(testProduct);

            UserProduct userProduct = userProductArgumentCaptor.getValue();
            assertThat(userProduct.getUser().getId()).isEqualTo(1);
            assertThat(userProduct.getProduct().getId()).isEqualTo(1);
            assertThat(userProduct.getUser().getUsername()).isEqualTo("TestUser");
            assertThat(userProduct.getProduct().getName()).isEqualTo("Test Product");
            assertThat(testProduct.getQuantity()).isEqualTo(98);

        }


        @Test
        @DisplayName("Increase quantity product to user if product is exist in cart")
        void addProductToUser_productInCart_increaseQuantity(){
            //Arrange
            UserProduct userProduct = new UserProduct();
            userProduct.setUser(testUser);
            userProduct.setProduct(testProduct);
            userProduct.setQuantity(10);
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.of(userProduct));

            //Act
            cartService.addProductToUser(1,1, 2);

            //Assert
            verify(productRepository).save(testProduct);

            assertThat(userProduct.getUser().getId()).isEqualTo(1);
            assertThat(userProduct.getProduct().getId()).isEqualTo(1);
            assertThat(userProduct.getUser().getUsername()).isEqualTo("TestUser");
            assertThat(userProduct.getProduct().getName()).isEqualTo("Test Product");
            assertThat(userProduct.getQuantity()).isEqualTo(12);
            assertThat(testProduct.getQuantity()).isEqualTo(98);
        }

        @Test
        @DisplayName("Throw Exception if product not exist")
        void addProductToUser_productNotExist_throwException(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> cartService.addProductToUser(1,1, 2)).isInstanceOf(NotFoundException.class);

        }

        @Test
        @DisplayName("Throw Exception if not enough product in storage")
        void addProductToUser_productNotEnoughInStorage_throwException(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));

            //Act & Assert
            assertThatThrownBy(() -> cartService.addProductToUser(1,1, 101)).isInstanceOf(OverLimitException.class);

        }

        @Test
        @DisplayName("Throw Exception if user not exist")
        void addProductToUser_userNotExist_throwException(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> cartService.addProductToUser(1,1, 2)).isInstanceOf(NotFoundException.class);

        }
    }

    @Nested
    @DisplayName("updateProductQuantity()")
    class UpdateProductQuantity{

        @Test
        @DisplayName("Update product quantity if all items are exists")
        void updateProductQuantity_allItemsExist_updateQuantity(){
            //Arrange
            UserProduct userProduct = new UserProduct();
            userProduct.setUser(testUser);
            userProduct.setProduct(testProduct);
            userProduct.setQuantity(10);
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.of(userProduct));

            //Act
            cartService.updateProductQuantity(1,1, 2);

            //Assert
            verify(productRepository).save(testProduct);

            assertThat(userProduct.getUser().getId()).isEqualTo(1);
            assertThat(userProduct.getProduct().getId()).isEqualTo(1);
            assertThat(userProduct.getUser().getUsername()).isEqualTo("TestUser");
            assertThat(userProduct.getProduct().getName()).isEqualTo("Test Product");
            assertThat(userProduct.getQuantity()).isEqualTo(2);
            assertThat(testProduct.getQuantity()).isEqualTo(108);
        }

        @Test
        @DisplayName("Delete product from cart if quantity is zero or below")
        void updateProductQuantity_quantityIsZero_deleteProduct(){
            //Arrange
            UserProduct userProduct = new UserProduct();
            userProduct.setUser(testUser);
            userProduct.setProduct(testProduct);
            userProduct.setQuantity(10);
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.of(userProduct));

            //Act
            cartService.updateProductQuantity(1,1, 0);

            //Assert
            verify(userProductRepository).delete(userProduct);
            verify(productRepository).save(testProduct);
            assertThat(testProduct.getQuantity()).isEqualTo(110);
        }

        @Test
        @DisplayName("Throw Exception if product not exist")
        void updateProductQuantity_productNotExist_throwException(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> cartService.updateProductQuantity(1,1, 0)).isInstanceOf(NotFoundException.class);

        }

        @Test
        @DisplayName("Throw Exception if UserProduct not exist")
        void updateProductQuantity_userProductNotExist_throwException(){
            //Arrange
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> cartService.updateProductQuantity(1,1, 0)).isInstanceOf(NotFoundException.class);

        }

        @Test
        @DisplayName("Throw Exception if not enough product in storage")
        void updateProductQuantity_notEnoughProductInStorage_throwException(){
            //Arrange
            UserProduct userProduct = new UserProduct();
            userProduct.setUser(testUser);
            userProduct.setProduct(testProduct);
            userProduct.setQuantity(10);
            when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(testProduct));
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.of(userProduct));

            //Act & Assert
            assertThatThrownBy(() -> cartService.updateProductQuantity(1,1, 111)).isInstanceOf(OverLimitException.class);

        }
    }
    @Nested
    @DisplayName("deleteProductFromUser()")
    class DeleteProductFromUser{
        @Test
        @DisplayName("Delete product from user if user and product exist")
        void deleteProductFromUser_userAndProductExists_deleteProduct(){
            //Arrange
            UserProduct userProduct = new UserProduct();
            userProduct.setUser(testUser);
            userProduct.setProduct(testProduct);
            userProduct.setQuantity(10);
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.of(userProduct));

            //Act
            cartService.deleteProductFromUser(1,1);

            //Assert
            verify(userProductRepository).delete(userProduct);
            verify(productRepository).save(testProduct);
            assertThat(testProduct.getQuantity()).isEqualTo(110);

        }

        @Test
        @DisplayName("Throw Exception if UserProduct not exist")
        void deleteProductFromUser_userProductNotExists_throwException(){
            //Arrange
            when(userProductRepository.findById(new UserProductId(1,1))).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> cartService.deleteProductFromUser(1,1)).isInstanceOf(NotFoundException.class);

        }
    }
}
