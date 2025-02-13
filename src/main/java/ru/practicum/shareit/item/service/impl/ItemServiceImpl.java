package ru.practicum.shareit.item.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
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
        User owner = userMapper.responseToUser(userService.getUser(ownerId));
        Item item = itemMapper.updateRequestToItem(updateItemRequest, owner, itemId);

        Item oldItem = getUpdatedOldItem(item, owner);
        itemRepository.save(oldItem);

        return itemMapper.itemToMergeResponse(item);
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

    @Override
    public List<ItemResponse> getAllUserItems(Long ownerId) {
        return itemRepository.findItemsByOwnerId(ownerId).stream()
                .map(itemMapper::itemToResponse).toList();
    }

    @Override
    public List<ItemResponse> searchItems(String query) {
        return itemRepository.findItemsByNameLikeIgnoreCaseAndAvailableTrue(query).stream()
                .map(itemMapper::itemToResponse).toList();
    }

    private Item getUpdatedOldItem(Item item, User owner) {
        Item oldItem = itemMapper.responseToItem(getItem(item.getId()), owner);
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
        return oldItem;
    }
}
