package ru.practicum.shareit.user.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserRepository;
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
        try {
            User user = userMapper.createRequestToUser(createUserRequest);
            return userMapper.userToMergeResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_email_key")) {
                throw new EmailAlreadyExistsException(String.format("User with email %s already exists", createUserRequest.getEmail()));
            } else {
                throw e;
            }
        }
    }

    @Override
    public MergeUserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
        try {
            User user = userMapper.updateRequestToUser(updateUserRequest, userId);
            User oldUser = getUpdatedOldUser(user);
            userRepository.save(oldUser);
            return userMapper.responseToMergeUserResponse(getUser(userId));
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_email_key")) {
                throw new EmailAlreadyExistsException(String.format("User with email %s already exists", updateUserRequest.getEmail()));
            } else {
                throw e;
            }
        }
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
