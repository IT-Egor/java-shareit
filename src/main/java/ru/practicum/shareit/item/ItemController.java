package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.MergeItemResponse;
import ru.practicum.shareit.item.service.ItemService;

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
}
