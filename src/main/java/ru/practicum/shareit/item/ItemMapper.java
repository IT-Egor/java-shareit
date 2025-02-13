package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "createItemRequest.name")
    Item createRequestToItem(CreateItemRequest createItemRequest, User owner);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "updateItemRequest.name")
    Item updateRequestToItem(UpdateItemRequest updateItemRequest, User owner);

    MergeItemResponse itemToMergeResponse(Item item);

    MergeItemResponse responseToMergeResponse(ItemResponse itemResponse);

    ItemResponse itemToResponse(Item item);
}
