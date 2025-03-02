package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private User updatedUser;
    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private UserResponse userResponse;
    private UserResponse updatedResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test Name");
        user.setEmail("test@example.com");

        createUserRequest = CreateUserRequest.builder().name("Test Name").email("test@example.com").build();
        updateUserRequest = UpdateUserRequest.builder().name("Updated Name").email("updated@example.com").build();
        userResponse = UserResponse.builder().id(1L).name("Test Name").email("test@example.com").build();

        updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");
        updatedResponse = UserResponse.builder().id(1L).name("Updated Name").email("updated@example.com").build();
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse actualResponse = userService.createUser(createUserRequest);
        assertThat(actualResponse).isEqualTo(userResponse);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenCreatingUserWithExistingEmail() {
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> userService.createUser(createUserRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    void shouldUpdateUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        UserResponse actualResponse = userService.updateUser(user.getId(), updateUserRequest);
        assertThat(actualResponse).isEqualTo(updatedResponse);
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenUpdatingUserWithExistingEmail() {
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
        assertThatThrownBy(() -> userService.updateUser(user.getId(), updateUserRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, times(0)).findById(anyLong());
        verify(userRepository, times(0)).save(any(User.class));
        verify(userRepository, times(1)).existsByEmail(any(String.class));
    }

    @Test
    void shouldFindUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        UserResponse actualResponse = userService.getUser(user.getId());
        assertThat(actualResponse).isEqualTo(userResponse);
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUpdatingNonExistingUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(1L, updateUserRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDeleteUser() {
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
    }
}