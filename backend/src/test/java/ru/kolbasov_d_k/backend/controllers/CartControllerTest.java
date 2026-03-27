package ru.kolbasov_d_k.backend.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kolbasov_d_k.backend.config.SpringSecurity;
import ru.kolbasov_d_k.backend.dto.CartItemResponseDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.CartService;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;
import ru.kolbasov_d_k.backend.services.ProductService;
import ru.kolbasov_d_k.backend.services.UserProductService;
import org.springframework.http.MediaType;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(SpringSecurity.class)
@DisplayName("CartControllerTest - slice tests")
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;
    @MockitoBean
    private ProductService productService;
    @MockitoBean
    private UserProductService userProductService;
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    private User testUser;
    private org.springframework.security.core.userdetails.User userDetails;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("TestUser@Test");

        userDetails = new org.springframework.security.core.userdetails.User(
                "TestUser@Test", "Password1",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        ){
            public User user(){
                return testUser;
            }
        };
    }

    @Nested
    @DisplayName("getAllProducts()")
    class GetAllProducts{

        @Test
        @DisplayName("Return all items in cart")
        @WithMockUser(authorities = "ROLE_USER")
        void getAllProducts_ifItemsExist_returnAllItems() throws Exception {
            //Arrange
            ProductResponseDTO productResponseDTO = new ProductResponseDTO();
            ProductResponseDTO productResponseDTO1 = new ProductResponseDTO();
            when(productService.findAll()).thenReturn(List.of(productResponseDTO1, productResponseDTO));

            //Act & Assert
            mockMvc.perform(get("/api/cart"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(productResponseDTO1.getId())))
                    .andExpect(jsonPath("$[1].id", is(productResponseDTO.getId())));

            verify(productService).findAll();
        }
        @Test
        @DisplayName("Return empty list if no items exist")
        @WithMockUser(authorities = "ROLE_USER")
        void getAllProducts_noItemsExist_returnEmptyList() throws Exception {
            //Arrange
            when(productService.findAll()).thenReturn(List.of());

            //Act & Assert
            mockMvc.perform(get("/api/cart"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
        @Test
        @DisplayName("Redirect if user is not authenticated")
        void getAllProducts_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(get("/api/cart"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("add()")
    class Add{
        @Test
        @DisplayName("Return 200 and empty list if user credentials are valid & no items exist")
        void add_credentialsValidAndNoItemsExist_return200AndEmptyList() throws Exception {
            //Arrange
            when(userProductService.findOrders(testUser)).thenReturn(List.of());
            //Act
            mockMvc.perform(post("/api/cart")
                    .with(user(userDetails))
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    //Assert
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(cartService).addProductToUser(1, 1, 2);
            verify(userProductService).findOrders(testUser);
        }
        @Test
        @DisplayName("Return 200 and list of cartItemResponseDTO if user credentials are valid & items exist")
        void add_credentialsValidAndItemsExist_return200AndListOfDTO() throws Exception {
            //Arrange
            when(userProductService.findOrders(testUser)).thenReturn(List.of(new CartItemResponseDTO()));
            //Act
            mockMvc.perform(post("/api/cart")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    //Assert
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
        @Test
        @DisplayName("Redirect if user is not authenticated")
        void add_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(post("/api/cart")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
        @Test
        @DisplayName("Return 403 if no csrf token")
        void add_csrfTokenNotFound_return403() throws Exception {
            //Act
            mockMvc.perform(post("/api/cart")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }
    @Nested
    @DisplayName("update()")
    class Update{
        @Test
        @DisplayName("Return 200 and empty list if user credentials are valid & no items exist")
        void update_credentialsValidAndNoItemsExist_return200AndEmptyList() throws Exception {
            //Arrange
            when(userProductService.findOrders(testUser)).thenReturn(List.of());
            //Act
            mockMvc.perform(patch("/api/cart")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    //Assert
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(cartService).updateProductQuantity(1, 1, 2);
            verify(userProductService).findOrders(testUser);
        }
        @Test
        @DisplayName("Return 200 and list of cartItemResponseDTO if user credentials are valid & items exist")
        void update_credentialsValidAndItemsExist_return200AndListOfDTO() throws Exception {
            //Arrange
            when(userProductService.findOrders(testUser)).thenReturn(List.of(new CartItemResponseDTO()));
            //Act
            mockMvc.perform(patch("/api/cart")
                            .with(user(userDetails))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    //Assert
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
        @Test
        @DisplayName("Redirect if user is not authenticated")
        void update_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(patch("/api/cart")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
        @Test
        @DisplayName("Return 403 if no csrf token")
        void update_csrfTokenNotFound_return403() throws Exception {
            //Act
            mockMvc.perform(patch("/api/cart")
                            .with(user(userDetails))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                            "productId": 1,
                            "quantity": 2
                            }
                    """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }
    @Nested
    @DisplayName("delete()")
    class Delete{
        @Test
        @DisplayName("Return 200 and empty list if user credentials are valid & no items exist")
        void delete_credentialsValidAndNoItemsExist_return200AndEmptyList() throws Exception {
            //Arrange
            when(userProductService.findOrders(testUser)).thenReturn(List.of());
            //Act
            mockMvc.perform(delete("/api/cart/1")
                            .with(user(userDetails))
                            .with(csrf()))
                    //Assert
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(cartService).deleteProductFromUser(1, 1);
            verify(userProductService).findOrders(testUser);
        }
        @Test
        @DisplayName("Redirect to auth modal if user is not authenticated")
        void delete_userNotAuthenticate_redirect() throws Exception {
            //Act
            mockMvc.perform(delete("/api/cart/1")
                            .with(csrf()))
                    //Assert
                    .andExpect(status().is3xxRedirection())
                    .andDo(print());
        }
        @Test
        @DisplayName("Return 403 if no csrf token")
        void delete_csrfTokenNotFound_return403() throws Exception {
            //Act
            mockMvc.perform(delete("/api/cart/1")
                            .with(user(userDetails)))
                    //Assert
                    .andExpect(status().isForbidden())
                    .andDo(print());
        }

    }

}
