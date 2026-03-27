package ru.kolbasov_d_k.backend.controllers;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kolbasov_d_k.backend.config.SpringSecurity;
import ru.kolbasov_d_k.backend.services.MyUserDetailsService;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@Import(SpringSecurity.class)
@DisplayName("SessionControllerTest - slice tests")
public class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Nested
    @DisplayName("checkSession()")
    class CheckSession {
        @Test
        @DisplayName("Return 204 if user is authenticated")
        @WithMockUser
        void checkSession_userIsAuthenticate_return204() throws Exception {
            mockMvc.perform(get("/api/session"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
        @Test
        @DisplayName("Return 200 if user is not authenticated")
        void checkSession_userIsNotAuthenticate_return200() throws Exception {
            mockMvc.perform(get("/api/session"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
        @Test
        @DisplayName("HEAD Return 204 if user is authenticated")
        @WithMockUser
        void checkSession_headAuthenticated_return204() throws Exception {
            mockMvc.perform(head("/api/session"))
                    .andDo(print())
                    .andExpect(status().isNoContent());
        }
        @Test
        @DisplayName("HEAD Return 200 if user is not authenticated")
        void checkSession_headNotAuthenticated_return200() throws Exception {
            mockMvc.perform(head("/api/session"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }
    }



}
