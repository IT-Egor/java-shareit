package ru.practicum.shareit.request.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private UserService userService;
    private RequestMapper requestMapper;
    private UserMapper userMapper;

    @Override
    public RequestResponse createRequest(RequestCreateRequest itemRequestCreateRequest, Long requesterId) {
        User requester = userMapper.responseToUser(userService.getUser(requesterId));
        Request request = requestMapper.requestCreateRequestToRequest(itemRequestCreateRequest, requester);
        request.setCreationDate(LocalDateTime.now());
        return requestMapper.requestToResponse(requestRepository.save(request));
    }
}
