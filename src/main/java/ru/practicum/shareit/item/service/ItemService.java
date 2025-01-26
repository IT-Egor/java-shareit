package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;

import java.util.List;

public interface ItemService {
    MergeItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId);

    MergeItemResponse updateItem(Long itemId, UpdateItemRequest updateItemRequest, Long ownerId);

    ItemResponse getItem(Long itemId);

    List<ItemResponse> getAllUserItems(Long ownerId);

    List<ItemResponse> searchItems(String query);
}
