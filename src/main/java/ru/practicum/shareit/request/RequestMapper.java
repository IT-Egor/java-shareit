package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "requester", source = "requester")
    @Mapping(target = "id", ignore = true)
    Request requestCreateRequestToRequest(RequestCreateRequest createItemRequest, User requester);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "created", source = "request.creationDate")
    RequestResponse requestToResponse(Request request);

    @Mapping(target = "answers", source = "answers")
    @Mapping(target = "created", source = "request.creationDate")
    RequestWithAnswersResponse requestToResponseWithAnswers(Request request, List<ItemResponse> answers);
}
