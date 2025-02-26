package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.service.RequestService;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {
    private RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse createRequest(@RequestBody RequestCreateRequest itemRequestCreateRequest,
                                         @RequestHeader(name = "X-Sharer-User-Id") Long requesterId) {

        return requestService.createRequest(itemRequestCreateRequest, requesterId);
    }
}
