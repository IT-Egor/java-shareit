package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    MergeItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId);

    MergeItemResponse updateItem(Long itemId, UpdateItemRequest updateItemRequest, Long ownerId);

    ItemResponse findItem(Long itemId);

    List<ItemResponse> getAllUserItems(Long ownerId);

    List<ItemResponse> searchItems(String query);

    MergeCommentResponse addComment(CreateCommentRequest createCommentRequest, Long itemId, Long authorId);
}
