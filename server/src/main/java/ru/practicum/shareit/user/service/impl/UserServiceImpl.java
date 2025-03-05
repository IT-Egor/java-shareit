package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.CreateUserRequest;
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
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    String.format("User with email %s already exists", createUserRequest.getEmail())
            );
        }
        User user = userMapper.createRequestToUser(createUserRequest);
        return userMapper.userToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new EmailAlreadyExistsException(
                    String.format("User with email %s already exists", updateUserRequest.getEmail())
            );
        }
        User user = userMapper.updateRequestToUser(updateUserRequest, userId);
        User oldUser = getUpdatedOldUser(user);
        return userMapper.userToResponse(userRepository.save(oldUser));
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
        userRepository.deleteById(userId);
    }

    private User getUpdatedOldUser(User user) {
        User oldUser = userMapper.responseToUser(getUser(user.getId()));
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return oldUser;
    }
}
