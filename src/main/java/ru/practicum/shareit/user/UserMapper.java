package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.MergeUserResponse;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    User createRequestToUser(CreateUserRequest createUserRequest);

    @Mapping(target = "id", source = "id")
    User updateRequestToUser(UpdateUserRequest updateUserRequest, Long id);

    MergeUserResponse userToMergeResponse(User user);

    MergeUserResponse responseToMergeUserResponse(UserResponse userResponse);

    UserResponse userToResponse(User user);

    User responseToUser(UserResponse userResponse);
}
