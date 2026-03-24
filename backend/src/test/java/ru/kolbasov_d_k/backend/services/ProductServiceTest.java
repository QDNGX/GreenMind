package ru.kolbasov_d_k.backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.kolbasov_d_k.backend.dto.ProductCreateDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.models.Product;
import ru.kolbasov_d_k.backend.repositories.ProductRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests - unit tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductCreateDTO testProductCreateDTO;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Ficus");
        testProduct.setPrice(new BigDecimal("29.99"));
        testProduct.setImagePath("https://example.com/image.jpg");
        testProduct.setQuantity(10);
        testProduct.setCreatedAt(LocalDateTime.of(2023, 1, 1, 0, 0));
        testProduct.setUpdatedAt(LocalDateTime.now());

        testProductCreateDTO = new ProductCreateDTO();
        testProductCreateDTO.setName("Test Product");
        testProductCreateDTO.setPrice(new BigDecimal("29.99"));
        testProductCreateDTO.setImagePath("https://example.com/image.jpg");
        testProductCreateDTO.setQuantity(10);
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        // ---------------- find all ------------------ //
        @Test
        @DisplayName("Returns the list of ProductResponseDTO, when products exist")
        void findAll_productExist_returnDTOList(){
            //Arrange
            Product second = new Product();
            second.setId(2);
            second.setName("Cactus");
            second.setPrice(new BigDecimal("9.99"));
            second.setImagePath("/img/cactus.jpg");
            second.setQuantity(25);
            second.setCreatedAt(LocalDateTime.of(2025, 2, 1, 12, 0));
            second.setUpdatedAt(LocalDateTime.of(2025, 2, 1, 12, 0));

            when(productRepository.findAll()).thenReturn(List.of(testProduct, second));

            //Act
            List<ProductResponseDTO> list = productService.findAll();

            //Assert
            assertThat(list).hasSize(2);
            assertThat(list.get(0).getName()).isEqualTo("Ficus");
            assertThat(list.get(1).getName()).isEqualTo("Cactus");

            verify(productRepository).findAll();
        }

        @Test
        @DisplayName("Returns empty list, when no products exist")
        void findAll_noProducts_returnsEmptyList(){
            //Arrange
            when(productRepository.findAll()).thenReturn(List.of());

            //Act
            List<ProductResponseDTO> list = productService.findAll();

            //Assert
            assertThat(list).isEmpty();

            verify(productRepository).findAll();
        }

    }

    // ---------------- find by id ------------------ //
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("Find product by id and return")
        void find_whenProductExist_returnProduct(){
            //Arrange
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

            //Act
            ProductResponseDTO findProduct = productService.findById(1);

            //Assert
            assertThat(findProduct.getName()).isEqualTo("Ficus");
            assertThat(findProduct.getPrice()).isEqualTo(new BigDecimal("29.99"));
            assertThat(findProduct.getQuantity()).isEqualTo(10);
            verify(productRepository).findById(1);
        }

        @Test
        @DisplayName("Throw exception if product not found")
        void find_whenProductNotFound_throwsException(){
            //Arrange
            when(productRepository.findById(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> productService.findById(1)).isInstanceOf(NotFoundException.class);

        }
    }

    // ---------------- create ------------------ //
    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("Save productDTO and returns")
        void create_validDTO_savesAndReturnDTO(){
            //Arrange
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            //Act
            ProductResponseDTO result = productService.create(testProductCreateDTO);

            //Assert
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Ficus");
            assertThat(result.getPrice()).isEqualTo(new BigDecimal("29.99"));
            assertThat(result.getImagePath()).isEqualTo("https://example.com/image.jpg");
            assertThat(result.getQuantity()).isEqualTo(10);

            ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
            verify(productRepository).save(productCaptor.capture());

            Product savedProduct = productCaptor.getValue();
            assertThat(savedProduct.getName()).isEqualTo("Test Product");
            assertThat(savedProduct.getPrice()).isEqualTo(new BigDecimal("29.99"));
            assertThat(savedProduct.getImagePath()).isEqualTo("https://example.com/image.jpg");
            assertThat(savedProduct.getQuantity()).isEqualTo(10);


        }

    }

    // ---------------- update ------------------ //
    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("Update productDTO and returns")
        void update_validDTO_savesAndReturnDTO(){
            //Arrange
            when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));
            when(productRepository.save(any(Product.class))).thenReturn(testProduct);

            //Act
            ProductResponseDTO updatedProduct1 = productService.update(1, testProductCreateDTO);

            //Assert
            assertThat(updatedProduct1.getName()).isEqualTo("Test Product");
            assertThat(updatedProduct1.getPrice()).isEqualTo(new BigDecimal("29.99"));
            assertThat(updatedProduct1.getImagePath()).isEqualTo("https://example.com/image.jpg");
            assertThat(updatedProduct1.getQuantity()).isEqualTo(10);

            verify(productRepository).findById(1);

        }

        @Test
        @DisplayName("Throw Exception if product not found")
        void update_whenProductNotFound_throwsException(){
            //Arrange
            when(productRepository.findById(1)).thenReturn(Optional.empty());

            //Act & assert
            assertThatThrownBy(() -> productService.update(1, testProductCreateDTO)).isInstanceOf(NotFoundException.class);
        }
    }

    // ---------------- delete ------------------ //
    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("Find entity and delete")
        void delete_ifEntityFound_andDelete(){
            //Arrange
            when(productRepository.existsById(1)).thenReturn(true);

            //Act
            productService.delete(1);

            //Assert
            verify(productRepository).deleteById(1);
        }

        @Test
        @DisplayName("Throw Exception of Entity not exist")
        void delete_whenEntityNotFound_throwsException(){
            //Arrange
            when(productRepository.existsById(1)).thenReturn(false);

            //Act & Assert
            assertThatThrownBy(() -> productService.delete(1)).isInstanceOf(NotFoundException.class);
            verify(productRepository, never()).deleteById(anyInt());
        }
    }

}
