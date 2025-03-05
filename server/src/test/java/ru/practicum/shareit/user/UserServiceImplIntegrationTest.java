package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.impl.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=shareit",
        "spring.datasource.password=shareit",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;

    public UserServiceImplIntegrationTest() {
        createUserRequest = CreateUserRequest.builder()
                .name("test name")
                .email("test@example.com")
                .build();

        updateUserRequest = UpdateUserRequest.builder()
                .name("update name")
                .email("update@example.com")
                .build();
    }

    @Test
    void shouldCreateUser() {
        UserResponse userResponse = userService.createUser(createUserRequest);

        assertThat(userResponse.getId()).isNotNull();
        assertThat(userResponse.getName()).isEqualTo("test name");
        assertThat(userResponse.getEmail()).isEqualTo("test@example.com");

        User savedUser = userRepository.findById(userResponse.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("test name");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenCreatingUserWithExistingEmail() {
        userService.createUser(createUserRequest);

        CreateUserRequest duplicateEmailRequest = CreateUserRequest.builder()
                .name("another name")
                .email("test@example.com")
                .build();

        assertThatThrownBy(() -> userService.createUser(duplicateEmailRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void shouldUpdateUser() {
        UserResponse createdUser = userService.createUser(createUserRequest);
        UserResponse updatedUser = userService.updateUser(createdUser.getId(), updateUserRequest);

        assertThat(updatedUser.getId()).isEqualTo(createdUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("update name");
        assertThat(updatedUser.getEmail()).isEqualTo("update@example.com");

        User savedUser = userRepository.findById(updatedUser.getId()).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("update name");
        assertThat(savedUser.getEmail()).isEqualTo("update@example.com");
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenUpdatingUserWithExistingEmail() {
        UserResponse firstUser = userService.createUser(createUserRequest);

        CreateUserRequest secondUserRequest = CreateUserRequest.builder()
                .name("second name")
                .email("second@example.com")
                .build();
        userService.createUser(secondUserRequest);

        UpdateUserRequest updateRequestWithExistingEmail = UpdateUserRequest.builder()
                .email("second@example.com")
                .build();

        assertThatThrownBy(() -> userService.updateUser(firstUser.getId(), updateRequestWithExistingEmail))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    void shouldGetUser() {
        UserResponse createdUser = userService.createUser(createUserRequest);
        UserResponse foundUser = userService.getUser(createdUser.getId());

        assertThat(foundUser.getId()).isEqualTo(createdUser.getId());
        assertThat(foundUser.getName()).isEqualTo("test name");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenGettingNonExistentUser() {
        assertThatThrownBy(() -> userService.getUser(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldDeleteUser() {
        UserResponse createdUser = userService.createUser(createUserRequest);
        userService.deleteUser(createdUser.getId());

        assertThatThrownBy(() -> userService.getUser(createdUser.getId()))
                .isInstanceOf(NotFoundException.class);
    }
}