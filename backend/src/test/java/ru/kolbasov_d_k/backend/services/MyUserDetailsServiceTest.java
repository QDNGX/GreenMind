package ru.kolbasov_d_k.backend.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kolbasov_d_k.backend.models.Role;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName( "MyUserDetailsService Test - Unit Tests")
public class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService myUserDetailsService;

    private User testUser;

    @BeforeEach
    void setUp(){
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("TestUser@Test");
        testUser.setPasswordHash("hashedPassword");
        testUser.setRole(Role.USER);
    }

    @Nested
    @DisplayName("loadUserByUsername()")
    class LoadUserByUsername {

        @Test
        @DisplayName("Return credentials of the user if user exist")
        void loadUserByUsername_userExist_returnCredentials(){
            //Arrange
            when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

            //Act
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(testUser.getEmail());

            //Assert
            assertThat(userDetails.getUsername()).isEqualTo("TestUser@Test");
            assertThat(userDetails.getPassword()).isEqualTo("hashedPassword");
            assertThat(userDetails.getAuthorities()).isNotNull();

        }
        @Test
        @DisplayName("Throw exception if user not exist")
        void loadUserByUsername_userNotExist_throwException(){
            //Arrange
            when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() ->  myUserDetailsService.loadUserByUsername(testUser.getEmail())).isInstanceOf(UsernameNotFoundException.class);
        }
    }
}
