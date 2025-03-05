package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);

    UserResponse updateUser(Long userid, UpdateUserRequest updateUserRequest);

    UserResponse getUser(Long userId);

    void deleteUser(Long userId);
}
