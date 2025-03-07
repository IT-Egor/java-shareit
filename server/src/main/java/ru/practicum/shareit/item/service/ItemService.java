package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId);

    ItemResponse updateItem(Long itemId, UpdateItemRequest updateItemRequest, Long ownerId);

    ItemResponse findItem(Long itemId);

    ItemResponseComments findItemWithComments(Long itemId);

    List<ItemResponseBookingComments> getAllUserItems(Long ownerId);

    List<ItemResponse> searchItems(String query);

    MergeCommentResponse addComment(CreateCommentRequest createCommentRequest, Long itemId, Long authorId);

    List<ItemResponse> findItemsByRequestIds(List<Long> requestIds);
}
