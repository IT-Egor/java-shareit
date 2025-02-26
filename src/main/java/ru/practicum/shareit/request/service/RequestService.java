package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;

public interface RequestService {
    ItemRequestResponse createRequest(ItemRequestCreateRequest itemRequestCreateRequest);
}
