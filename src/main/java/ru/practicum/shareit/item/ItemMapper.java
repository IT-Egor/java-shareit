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
    @Mapping(target = "id", source = "id")
    Item updateRequestToItem(UpdateItemRequest updateItemRequest, User owner, Long id);

    @Mapping(target = "ownerId", source = "owner.id")
    MergeItemResponse itemToMergeResponse(Item item);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemResponse itemToResponse(Item item);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "name", source = "itemResponse.name")
    @Mapping(target = "id", source = "itemResponse.id")
    Item responseToItem(ItemResponse itemResponse, User owner);
}
