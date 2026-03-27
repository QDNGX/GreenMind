package ru.kolbasov_d_k.backend.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kolbasov_d_k.backend.config.SpringSecurity;
import ru.kolbasov_d_k.backend.dto.CartItemResponseDTO;
import ru.kolbasov_d_k.backend.dto.ProductResponseDTO;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;
import ru.kolbasov_d_k.backend.services.ProductService;
import ru.kolbasov_d_k.backend.services.UserService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SpringSecurity.class)
@DisplayName("AdminControllerTest - slice tests")
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private ProductService productService;

    private UserResponseDTO testUserDTO;
    private ProductResponseDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserResponseDTO();
        testUserDTO.setId(1);
        testUserDTO.setUsername("testUser");
        testUserDTO.setEmail("TestUser@Test");

        testProductDTO = new ProductResponseDTO();
        testProductDTO.setId(1);
        testProductDTO.setName("Test Product");
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {
        @Test
        @DisplayName("Return all users for admin")
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_asAdmin_returnAllUsers() throws Exception {
            //Arrange
            when(userService.getAllUsers()).thenReturn(List.of(testUserDTO));

            //Act
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].username", is("testUser")));
        }

        @Test
        @DisplayName("Return empty list if no users exist")
        @WithMockUser(roles = "ADMIN")
        void getAllUsers_noUsersExist_returnEmptyList() throws Exception {
            //Arrange
            when(userService.getAllUsers()).thenReturn(List.of());

            //Act
            mockMvc.perform(get("/api/admin/users"))
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void getAllUsers_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Redirect if user is not authenticated")
        void getAllUsers_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(get("/api/admin/users"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("getAllProducts()")
    class GetAllProducts {
        @Test
        @DisplayName("Return all products for admin")
        @WithMockUser(roles = "ADMIN")
        void getAllProducts_asAdmin_returnAllProducts() throws Exception {
            //Arrange
            when(productService.findAll()).thenReturn(List.of(testProductDTO));

            //Act
            mockMvc.perform(get("/api/admin/products"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name", is("Test Product")));
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void getAllProducts_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(get("/api/admin/products"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("getProductById()")
    class GetProductById {
        @Test
        @DisplayName("Return product by id for admin")
        @WithMockUser(roles = "ADMIN")
        void getProductById_asAdmin_returnProduct() throws Exception {
            //Arrange
            when(productService.findById(1)).thenReturn(testProductDTO);

            //Act
            mockMvc.perform(get("/api/admin/products/1"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is("Test Product")));
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void getProductById_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(get("/api/admin/products/1"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("createProduct()")
    class CreateProduct {
        @Test
        @DisplayName("Return 201 if product created successfully")
        @WithMockUser(roles = "ADMIN")
        void createProduct_validData_return201() throws Exception {
            //Arrange
            when(productService.create(any())).thenReturn(testProductDTO);

            //Act
            mockMvc.perform(post("/api/admin/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Test Product",
                                "price": 99.99,
                                "imagePath": "/img/test.png",
                                "quantity": 10
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is("Test Product")));
        }

        @Test
        @DisplayName("Return 400 if product name is blank")
        @WithMockUser(roles = "ADMIN")
        void createProduct_nameBlank_return400() throws Exception {
            //Act
            mockMvc.perform(post("/api/admin/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "",
                                "price": 99.99,
                                "quantity": 10
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Return 400 if price is null")
        @WithMockUser(roles = "ADMIN")
        void createProduct_priceNull_return400() throws Exception {
            //Act
            mockMvc.perform(post("/api/admin/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Test Product",
                                "quantity": 10
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Return 403 if no csrf token")
        @WithMockUser(roles = "ADMIN")
        void createProduct_noCsrf_return403() throws Exception {
            //Act
            mockMvc.perform(post("/api/admin/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Test Product",
                                "price": 99.99,
                                "quantity": 10
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void createProduct_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(post("/api/admin/products")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Test Product",
                                "price": 99.99,
                                "quantity": 10
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("updateProduct()")
    class UpdateProduct {
        @Test
        @DisplayName("Return 200 if product updated successfully")
        @WithMockUser(roles = "ADMIN")
        void updateProduct_validData_return200() throws Exception {
            //Arrange
            when(productService.update(eq(1), any())).thenReturn(testProductDTO);

            //Act
            mockMvc.perform(put("/api/admin/products/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Updated Product",
                                "price": 149.99,
                                "imagePath": "/img/updated.png",
                                "quantity": 5
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Return 400 if validation fails")
        @WithMockUser(roles = "ADMIN")
        void updateProduct_invalidData_return400() throws Exception {
            //Act
            mockMvc.perform(put("/api/admin/products/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "",
                                "price": -1,
                                "quantity": -5
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Return 403 if no csrf token")
        @WithMockUser(roles = "ADMIN")
        void updateProduct_noCsrf_return403() throws Exception {
            //Act
            mockMvc.perform(put("/api/admin/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "name": "Updated Product",
                                "price": 149.99,
                                "quantity": 5
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("deleteProduct()")
    class DeleteProduct {
        @Test
        @DisplayName("Return 204 if product deleted successfully")
        @WithMockUser(roles = "ADMIN")
        void deleteProduct_asAdmin_return204() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/products/1")
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void deleteProduct_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/products/1")
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Return 403 if no csrf token")
        @WithMockUser(roles = "ADMIN")
        void deleteProduct_noCsrf_return403() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/products/1"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Redirect if user is not authenticated")
        void deleteProduct_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/products/1")
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUser {
        @Test
        @DisplayName("Return 204 if user deleted successfully")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_asAdmin_return204() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/users/1")
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void deleteUser_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/users/1")
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Return 403 if no csrf token")
        @WithMockUser(roles = "ADMIN")
        void deleteUser_noCsrf_return403() throws Exception {
            //Act
            mockMvc.perform(delete("/api/admin/users/1"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("updateUserRole()")
    class UpdateUserRole {
        @Test
        @DisplayName("Return 200 if role updated successfully")
        @WithMockUser(roles = "ADMIN")
        void updateUserRole_validRole_return200() throws Exception {
            //Arrange
            when(userService.updateUserRole(eq(1), eq("ADMIN"))).thenReturn(testUserDTO);

            //Act
            mockMvc.perform(patch("/api/admin/users/1/role")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "role": "ADMIN"
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Return 400 if role is blank")
        @WithMockUser(roles = "ADMIN")
        void updateUserRole_roleBlank_return400() throws Exception {
            //Act
            mockMvc.perform(patch("/api/admin/users/1/role")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "role": ""
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Return 400 if role is missing")
        @WithMockUser(roles = "ADMIN")
        void updateUserRole_roleMissing_return400() throws Exception {
            //Act
            mockMvc.perform(patch("/api/admin/users/1/role")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {}
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void updateUserRole_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(patch("/api/admin/users/1/role")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "role": "ADMIN"
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Return 403 if no csrf token")
        @WithMockUser(roles = "ADMIN")
        void updateUserRole_noCsrf_return403() throws Exception {
            //Act
            mockMvc.perform(patch("/api/admin/users/1/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "role": "ADMIN"
                            }
                            """))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("getUserOrders()")
    class GetUserOrders {
        @Test
        @DisplayName("Return user orders for admin")
        @WithMockUser(roles = "ADMIN")
        void getUserOrders_asAdmin_returnOrders() throws Exception {
            //Arrange
            when(userService.getUserOrders(1)).thenReturn(List.of(new CartItemResponseDTO()));

            //Act
            mockMvc.perform(get("/api/admin/users/1/orders"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("Return empty list if user has no orders")
        @WithMockUser(roles = "ADMIN")
        void getUserOrders_noOrders_returnEmptyList() throws Exception {
            //Arrange
            when(userService.getUserOrders(1)).thenReturn(List.of());

            //Act
            mockMvc.perform(get("/api/admin/users/1/orders"))
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Return 403 for regular user")
        @WithMockUser(roles = "USER")
        void getUserOrders_asUser_return403() throws Exception {
            //Act
            mockMvc.perform(get("/api/admin/users/1/orders"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
    }
}
