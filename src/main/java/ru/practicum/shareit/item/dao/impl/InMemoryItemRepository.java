package ru.practicum.shareit.item.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item save(Item item) {
        long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Long update(Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Item with id %d not found", item.getId()));
        }

        Item oldItem = items.get(itemId);
        if (!item.getOwner().getId()
                .equals(oldItem.getOwner().getId())) {
            throw new AuthorizationException("Authorization failed");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(itemId, oldItem);
        return 1L;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> findAllUserItems(Long userId) {
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).toList();
    }

    @Override
    public List<Item> search(String query) {
        if (query.isBlank()) {
            return List.of();
        }
        String lowerQuery = query.toLowerCase();
        return items.values().stream().filter(item ->
                (item.getName().toLowerCase().contains(lowerQuery)
                    || item.getDescription().toLowerCase().contains(lowerQuery)
                ) && item.getAvailable())
                .toList();
    }

    private long getNextId() {
        long maxId = items.keySet().stream()
                .max(Long::compareTo)
                .orElse(1L);
        return ++maxId;
    }
}
