package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;

public interface RequestService {
    RequestResponse createRequest(RequestCreateRequest itemRequestCreateRequest, Long requesterId);
}
