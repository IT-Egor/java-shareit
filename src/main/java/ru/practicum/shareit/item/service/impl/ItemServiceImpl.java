package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public MergeItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId) {
        User user = userMapper.responseToUser(userService.getUser(ownerId));
        Item item = itemMapper.createRequestToItem(createItemRequest, user);

        return itemMapper.itemToMergeResponse(itemRepository.save(item));
    }

    @Override
    public MergeItemResponse updateItem(Long itemId, UpdateItemRequest updateItemRequest, Long ownerId) {
        User user = userMapper.responseToUser(userService.getUser(ownerId));
        Item item = itemMapper.updateRequestToItem(updateItemRequest, user);
        itemRepository.update(itemId, item);

        return itemMapper.responseToMergeResponse(getItem(itemId));
    }

    @Override
    public ItemResponse getItem(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            return itemMapper.itemToResponse(itemOpt.get());
        } else {
            throw new NotFoundException(String.format("Item with id %s not found", itemId));
        }
    }
}
