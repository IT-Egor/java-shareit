package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.MergeItemResponse;

public interface ItemService {
    MergeItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId);
}
