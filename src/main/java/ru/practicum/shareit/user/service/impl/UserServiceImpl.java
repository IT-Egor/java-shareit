package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.MergeUserResponse;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public MergeUserResponse createUser(CreateUserRequest createUserRequest) {
        User user = userMapper.createRequestToUser(createUserRequest);
        return userMapper.userToMergeResponse(userRepository.save(user));
    }

    @Override
    public MergeUserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        User user = userMapper.updateRequestToUser(updateUserRequest);
        userRepository.update(userId, user);
        return userMapper.responseToMergeUserResponse(getUser(userId));
    }

    @Override
    public UserResponse getUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return userMapper.userToResponse(userOpt.get());
        } else {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.delete(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", userId));
        }
    }
}
