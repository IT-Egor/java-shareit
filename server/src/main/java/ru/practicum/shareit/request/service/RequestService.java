package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;

import java.util.List;

public interface RequestService {
    RequestResponse createRequest(RequestCreateDto itemRequestCreateDto, Long requesterId);

    List<RequestWithAnswersResponse> findAllUserRequests(Long requesterId);

    List<RequestResponse> findAllRequests();

    RequestWithAnswersResponse findRequestById(Long id);
}
