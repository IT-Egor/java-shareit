package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Long update(Long itemId, Item item);

    Optional<Item> findById(Long itemId);

    List<Item> findAllUserItems(Long userId);
}
