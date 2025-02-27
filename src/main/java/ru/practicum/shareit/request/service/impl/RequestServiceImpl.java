package ru.practicum.shareit.request.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private RequestRepository requestRepository;
    private UserService userService;
    private ItemService itemService;
    private RequestMapper requestMapper;
    private UserMapper userMapper;

    @Override
    public RequestResponse createRequest(RequestCreateRequest itemRequestCreateRequest, Long requesterId) {
        User requester = userMapper.responseToUser(userService.getUser(requesterId));
        Request request = requestMapper.requestCreateRequestToRequest(itemRequestCreateRequest, requester);
        request.setCreationDate(LocalDateTime.now());
        return requestMapper.requestToResponse(requestRepository.save(request));
    }

    @Override
    public List<RequestWithAnswersResponse> findAllUserRequests(Long requesterId) {
        List<Request> userRequests = requestRepository.findRequestsByRequester_IdOrderByCreationDateDesc(requesterId);
        List<ItemResponse> answers = itemService.findItemsByRequestIds(userRequests.stream().map(Request::getId).toList());

        Map<Long, List<ItemResponse>> answersByRequestId = answers.stream().collect(Collectors.groupingBy(ItemResponse::getRequestId));

        return userRequests.stream()
                .map(request ->
                        requestMapper.requestToResponseWithAnswers(
                                request,
                                answersByRequestId.getOrDefault(request.getId(), List.of())
                        )
                ).toList();
    }
}
