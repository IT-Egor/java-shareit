package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;

@Mapper
public interface UserMapper {
    User createRequestToUser(CreateUserRequest createUserRequest);

    @Mapping(target = "id", source = "id")
    User updateRequestToUser(UpdateUserRequest updateUserRequest, Long id);

    UserResponse userToResponse(User user);

    User responseToUser(UserResponse userResponse);
}
