package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ItemMapper {

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "createItemRequest.name")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "request", source = "request")
    @Mapping(target = "description", source = "createItemRequest.description")
    Item createRequestToItem(CreateItemRequest createItemRequest, User owner, Request request);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "updateItemRequest.name")
    @Mapping(target = "id", source = "id")
    Item updateRequestToItem(UpdateItemRequest updateItemRequest, User owner, Long id);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemResponse itemToResponse(Item item);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "itemResponse.name")
    @Mapping(target = "id", source = "itemResponse.id")
    Item responseToItem(ItemResponse itemResponse, User owner);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "ownerId", source = "item.owner.id")
    ItemResponseComments itemToResponseComments(Item item, List<ItemCommentResponse> comments);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "ownerId", source = "item.owner.id")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "lastBooking", source = "lastBooking")
    ItemResponseBookingComments itemToResponseBookingComments(Item item,
                                                              LocalDateTime nextBooking,
                                                              LocalDateTime lastBooking,
                                                              List<ItemCommentResponse> comments);
}
