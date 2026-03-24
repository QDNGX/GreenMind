package ru.kolbasov_d_k.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kolbasov_d_k.backend.dto.CartItemResponseDTO;
import ru.kolbasov_d_k.backend.dto.UserDTO;
import ru.kolbasov_d_k.backend.dto.UserResponseDTO;
import ru.kolbasov_d_k.backend.models.Role;
import ru.kolbasov_d_k.backend.models.User;
import ru.kolbasov_d_k.backend.repositories.UserRepository;
import ru.kolbasov_d_k.backend.utils.exceptions.NotFoundException;
import ru.kolbasov_d_k.backend.utils.exceptions.UnauthorizedClientException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Unit Tests")
public class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserProductService userProductService;
    @Mock
    private Principal principal;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setEmail("TestUser@Test");
        testUser.setRole(Role.USER);
        testUser.setBirthDate(LocalDate.parse("2000-01-01"));

        testUserDTO = new UserDTO();
        testUserDTO.setUsername("testUser");
        testUserDTO.setEmail("TestUser@Test");
        testUserDTO.setPassword("testPassword");
    }

    @Nested
    @DisplayName("findById()")
    class FindById {
        @Test
        @DisplayName("Return User if exist")
        void findById_userExists_returnsUser(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

            //Act
            User foundUser = userService.findById(1);

            //Assert
            assertThat(foundUser.getUsername()).isEqualTo("testUser");
            assertThat(foundUser.getEmail()).isEqualTo("TestUser@Test");
            verify(userRepository).findById(1);
        }

        @Test
        @DisplayName("Return null if user not exist")
        void findById_userNotExist_returnNull(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            //Act
            User foundUser = userService.findById(1);

            //Assert
            assertThat(foundUser).isNull();
        }

    }
    @Nested
    @DisplayName("getCurrentUser()")
    class GetCurrentUser {

        @Test
        @DisplayName("Get User if principal exist")
        void getCurrentUser_principalExists_returnsUser(){
            //Arrange
            when(principal.getName()).thenReturn("TestUser@Test");
            when(userRepository.findByEmail("TestUser@Test")).thenReturn(Optional.of(testUser));

            //Act
            User foundUser = userService.getCurrentUser(principal);

            //Assert
            assertThat(foundUser.getUsername()).isEqualTo("testUser");
            assertThat(foundUser.getEmail()).isEqualTo("TestUser@Test");
        }

        @Test
        @DisplayName("Throw Exception if principal not exist")
        void getCurrentUser_principalNotExists_throwsException(){
            //Arrange
            principal = null;
            //Act & Assert
            assertThatThrownBy(() -> userService.getCurrentUser(principal)).isInstanceOf(UnauthorizedClientException.class);
        }


    }
    @Nested
    @DisplayName("create()")
    class Create {
        @Test
        @DisplayName("Create User if DTO exist")
        void create_dtoExist_createsUser(){
            //Arrange
            when(passwordEncoder.encode(testUserDTO.getPassword())).thenReturn("hashedPassword");

            //Act
            userService.create(testUserDTO);

            //Assert
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getUsername()).isEqualTo("testUser");
            assertThat(savedUser.getEmail()).isEqualTo("TestUser@Test");
            assertThat(savedUser.getPasswordHash()).isEqualTo("hashedPassword");
            assertThat(savedUser.getRole()).isEqualTo(Role.USER);
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUser {
        @Test
        @DisplayName("Delete User if Exist")
        void delete_userExist_andDelete(){
            //Arrange
            when(userRepository.existsById(1)).thenReturn(true);

            //Act
            userService.deleteUser(1);

            //Assert
            verify(userRepository).deleteById(1);
        }

        @Test
        @DisplayName("Throw Exception if User not Exist")
        void delete_userNotExist_throwException(){
            //Arrange
            when(userRepository.existsById(1)).thenReturn(false);

            //Act & Assert
            assertThatThrownBy(() -> userService.deleteUser(1)).isInstanceOf(NotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {
        @Test
        @DisplayName("Return the list of UserResponseDTO if users exist")
        void getAllUsers_ifUsersExist_allUsersToList(){
            //Arrange
            User secondUser = new User();
            secondUser.setId(2);
            secondUser.setUsername("secondUser");
            secondUser.setEmail("SecondUser@Test");
            secondUser.setRole(Role.USER);
            secondUser.setBirthDate(LocalDate.parse("2000-01-02"));

            when(userRepository.findAll()).thenReturn(List.of(testUser, secondUser));

            //Act
            List<UserResponseDTO> listOfUsers = userService.getAllUsers();

            //Assert
            assertThat(listOfUsers).hasSize(2);
            assertThat(listOfUsers.get(0).getUsername()).isEqualTo("testUser");
            assertThat(listOfUsers.get(1).getUsername()).isEqualTo("secondUser");
        }

        @Test
        @DisplayName("Return empty list if users not exist")
        void getAllUsers_ifUsersNotExist_returnsEmptyList(){
            //Arrange
            when(userRepository.findAll()).thenReturn(List.of());

            //Act
            List<UserResponseDTO> listOfUsers = userService.getAllUsers();

            //Assert
            assertThat(listOfUsers).isEmpty();
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("updateProfile()")
    class UpdateProfile {
        @Test
        @DisplayName("Update all fields and assert")
        void updateProfile_allFieldsUpdated_assert(){
            //Act
            userService.updateProfile(testUser, "newUsername", "newEmail", LocalDate.parse("2000-01-02"));
            //Assert
            assertThat(testUser.getUsername()).isEqualTo("newUsername");
            assertThat(testUser.getEmail()).isEqualTo("newEmail");
            assertThat(testUser.getBirthDate()).isEqualTo(LocalDate.parse("2000-01-02"));
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Update username only and assert")
        void updateProfile_someFieldsNull_updatesOnlyProvided(){
            //Act
            userService.updateProfile(testUser, "updatedUser", null, null);
            //Assert
            assertThat(testUser.getUsername()).isEqualTo("updatedUser");
            assertThat(testUser.getEmail()).isEqualTo("TestUser@Test");
            assertThat(testUser.getBirthDate()).isEqualTo(LocalDate.parse("2000-01-01"));
            verify(userRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("updateUserRole()")
    class updateUserRole {
        @Test
        @DisplayName("Update User and Return DTO")
        void updateUserRole_roleIsValid_returnsDTO(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userRepository.save(testUser)).thenReturn(testUser);

            //Act
            UserResponseDTO updatedUser = userService.updateUserRole(1, "ADMIN");

            //Assert
            assertThat(updatedUser.getUsername()).isEqualTo("testUser");
            assertThat(updatedUser.getEmail()).isEqualTo("TestUser@Test");
            assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("Throw exception if user not exist")
        void updateUserRole_userNotExist_throwException(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> userService.updateUserRole(1, "ADMIN")).isInstanceOf(NotFoundException.class);
            verify(userRepository, never()).save(any());

        }

        @Test
        @DisplayName("Throw exception if role not valid")
        void updateUserRole_roleNotValid_throwException(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

            //Act & Assert
            assertThatThrownBy(() -> userService.updateUserRole(1, "Manager")).isInstanceOf(IllegalArgumentException.class);
            verify(userRepository, never()).save(any());

        }
    }

    @Nested
    @DisplayName("getUserOrders()")
    class getUserOrders{
        @Test
        @DisplayName("Return empty list if user  exist")
        void getUserOrders_userExist_returnEmptyList(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
            when(userProductService.findOrders(testUser)).thenReturn(List.of());

            //Act
            List<CartItemResponseDTO> cartItemResponseDTOList = userService.getUserOrders(1);

            //Assert
            assertThat(cartItemResponseDTOList).isEmpty();
        }

        @Test
        @DisplayName("Throw Exception if user not exist")
        void getUserOrders_userNotExist_throwException(){
            //Arrange
            when(userRepository.findById(1)).thenReturn(Optional.empty());

            //Act & Assert
            assertThatThrownBy(() -> userService.getUserOrders(1)).isInstanceOf(NotFoundException.class);

        }
    }
}
