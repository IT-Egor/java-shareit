package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testCreateUser() throws Exception {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("test name")
                .email("test@example.com")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("test name")
                .email("test@example.com")
                .build();

        Mockito.when(userService.createUser(Mockito.any(CreateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test name"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
                .name("updated test name")
                .email("updated_test@example.com")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("updated test name")
                .email("updated_test@example.com")
                .build();

        Mockito.when(userService.updateUser(Mockito.eq(1L), Mockito.any(UpdateUserRequest.class))).thenReturn(userResponse);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated test name"))
                .andExpect(jsonPath("$.email").value("updated_test@example.com"));
    }

    @Test
    void testGetUser() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .name("test name")
                .email("test@example.com")
                .build();

        Mockito.when(userService.getUser(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test name"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}