package ru.kolbasov_d_k.backend.controllers;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kolbasov_d_k.backend.config.SpringSecurity;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;
import ru.kolbasov_d_k.backend.services.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(RegisterController.class)
@Import(SpringSecurity.class)
@DisplayName("RegisterController - slice tests")
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Nested
    @DisplayName("register()")
    class Register {
        @Test
        @DisplayName("Return 200 if register is ok")
        void register_validData_return200() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(false);

            //Act
            mockMvc.perform(post("/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                            {
                                "username": "testUser",
                                "email": "TestUser@Test",
                                "password": "Password1234567890"
                            }
                            """)
                            .with(csrf()))
                    //Assert
                    .andExpect(status().isOk());

            verify(userService).existsByEmail("TestUser@Test");
            verify(userService).create(any());
        }
        @Test
        @DisplayName("Return 400 if username not valid")
        void register_userNameNotValid_return400() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(false);

            //Act
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "username": "",
                                "email": "TestUser@Test",
                                "password": "Password1234567890"
                            }
                            """)
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Return 400 if email not valid")
        void register_emailNotValid_return400() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(false);

            //Act
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "username": "testUser",
                                "email": "",
                                "password": "Password1234567890"
                            }
                            """)
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Return 400 if password not valid")
        void register_passwordNotValid_return400() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(false);

            //Act
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "username": "testUser",
                                "email": "TestUser@Test",
                                "password": "password"
                            }
                            """)
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isBadRequest());
        }
        @Test
        @DisplayName("Return 409 if email is duplicate")
        void register_emailIsDuplicate_throwException() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(true);

            //Act
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "username": "testUser",
                                "email": "TestUser@Test",
                                "password": "Password1234"
                            }
                            """)
                            .with(csrf()))
                    .andDo(print())
                    //Assert
                    .andExpect(status().isConflict());

            verify(userService).existsByEmail("TestUser@Test");
            verify(userService, never()).create(any());
        }
        @Test
        @DisplayName("Return 403 if json without csrf")
        void register_withoutCsrf_return403() throws Exception{
            //Arrange
            when(userService.existsByEmail("TestUser@Test")).thenReturn(false);

            //Act
            mockMvc.perform(post("/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                            {
                                "username": "testUser",
                                "email": "TestUser@Test",
                                "password": "Password1234567890"
                            }
                            """))
                    //Assert
                    .andExpect(status().isForbidden());
        }


    }



}
