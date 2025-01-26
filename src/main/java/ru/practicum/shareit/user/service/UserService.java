package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.MergeUserResponse;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

public interface UserService {
    MergeUserResponse createUser(CreateUserRequest createUserRequest);

    MergeUserResponse updateUser(Long userid, UpdateUserRequest updateUserRequest);

    UserResponse getUser(Long userId);

    void deleteUser(Long userId);
}
