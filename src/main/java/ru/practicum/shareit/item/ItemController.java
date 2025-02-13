package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MergeItemResponse createItem(@Valid @RequestBody CreateItemRequest createItemRequest,
                                        @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return itemService.createItem(createItemRequest, ownerId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public MergeItemResponse updateItem(@Valid @RequestBody UpdateItemRequest updateItemRequest,
                                        @PathVariable Long itemId,
                                        @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return itemService.updateItem(itemId, updateItemRequest, ownerId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemResponse getItem(@PathVariable(required = false) Long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> getAllUserItems(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return itemService.getAllUserItems(ownerId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemResponse> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}