package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        emailDoesNotExistCheck(user.getEmail());
        long id = getNextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public Long update(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("User with id %d not found", user.getId()));
        }
        emailDoesNotExistCheck(user.getEmail());
        User oldUser = users.get(userId);
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        users.put(userId, oldUser);
        return 1L;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public boolean delete(Long userId) {
        if (!users.containsKey(userId)) {
            return false;
        } else {
            users.remove(userId);
            return true;
        }
    }

    private void emailDoesNotExistCheck(String email) {
        if (users.values().stream().anyMatch(user -> user.getEmail().equals(email))) {
            throw new EmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }

    private long getNextId() {
        long maxId = users.keySet().stream()
                .max(Long::compareTo)
                .orElse(1L);
        return ++maxId;
    }
}
