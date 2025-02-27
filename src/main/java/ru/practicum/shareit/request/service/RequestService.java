package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;

import java.util.List;

public interface RequestService {
    RequestResponse createRequest(RequestCreateRequest itemRequestCreateRequest, Long requesterId);

    List<RequestWithAnswersResponse> findAllUserRequests(Long requesterId);
}
