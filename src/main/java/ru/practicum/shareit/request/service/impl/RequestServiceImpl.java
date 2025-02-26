package ru.practicum.shareit.request.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.request.service.RequestService;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private RequestMapper requestMapper;

    @Override
    public ItemRequestResponse createRequest(ItemRequestCreateRequest itemRequestCreateRequest) {
        return null;
    }
}
