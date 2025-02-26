package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.dto.RequestCreateRequest;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestMapper {
    @Mapping(target = "requester", source = "requester")
    Request requestCreateRequestToRequest(RequestCreateRequest createItemRequest, User requester);

    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "created", source = "request.creationDate")
    RequestResponse requestToResponse(Request request);
}
