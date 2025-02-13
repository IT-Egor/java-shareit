package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Long update(Long userId, User user);

    Optional<User> findById(Long userId);

    boolean delete(Long userId);
}
