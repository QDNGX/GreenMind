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
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;
import ru.kolbasov_d_k.backend.services.UserProductService;
import ru.kolbasov_d_k.backend.services.UserService;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
@Import(SpringSecurity.class)
@DisplayName("UserProfileControllerTest - slice tests")
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserProductService userProductService;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("TestUser@Test");
    }

    @Nested
    @DisplayName("getProfile()")
    class GetProfile {
        @Test
        @DisplayName("Return 200 if user is authenticated")
        @WithMockUser
        void getProfile_userIsAuthenticate_return200() throws Exception {
            //Arrange
            when(userService.getCurrentUser(any())).thenReturn(testUser);
            //Act
            mockMvc.perform(get("/api/profile"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("testUser"))
                    .andExpect(jsonPath("$.email").value("TestUser@Test"));

            verify(userService).getCurrentUser(any());
            verify(userProductService).findOrders(testUser);
        }
        @Test
        @DisplayName("Redirect if user is not authenticated")
        void getProfile_userIsNotAuthenticate_redirect() throws Exception {
            //Act
            mockMvc.perform(get("/api/profile"))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }

    }
    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {
        @Test
        @DisplayName("Return 200 if user is successfully updated")
        @WithMockUser
        void updateProfile_userIsAuthenticate_return200() throws Exception {
            //Arrange
            when(userService.getCurrentUser(any())).thenReturn(testUser);
            //Act
            mockMvc.perform(patch("/api/profile")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
{
"username": "testUser",
"email": "TestUser@Test",
"birthDate": "1990-01-01"
}"""))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("success"))
                    .andExpect(jsonPath("$.message").value("Профиль успешно обновлён"));

            verify(userService).getCurrentUser(any());
            verify(userService).updateProfile(eq(testUser), eq("testUser"), eq("TestUser@Test"), any());
        }
        @Test
        @DisplayName("Return 403 if csrf token is missing")
        @WithMockUser
        void updateProfile_csrfTokenNotFound_return403() throws Exception {
            //Arrange
            when(userService.getCurrentUser(any())).thenReturn(testUser);
            //Act
            mockMvc.perform(patch("/api/profile")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "username": "testUser",
                                              "email": "TestUser@Test",
                                      "birthDate": "1990-01-01"
                                      }"""))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isForbidden());
        }
        @Test
        @DisplayName("Redirect if user is not authenticated")
        void updateProfile_notAuthenticated_redirect() throws Exception {
            //Act
            mockMvc.perform(patch("/api/profile")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
{
"username": "testUser",
"email": "TestUser@Test",
"birthDate": "1990-01-01"
}"""))
                    .andDo(print())
                    //Assert
                    .andExpect(status().is3xxRedirection());
        }
    }



}
