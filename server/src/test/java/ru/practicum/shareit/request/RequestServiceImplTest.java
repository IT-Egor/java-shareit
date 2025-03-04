package ru.practicum.shareit.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Spy
    private RequestMapper requestMapper = Mappers.getMapper(RequestMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private RequestServiceImpl requestService;

    private User user;
    private UserResponse userResponse;
    private Request request;
    private RequestCreateDto requestCreateDto;
    private RequestResponse requestResponse;
    private RequestWithAnswersResponse requestWithAnswersResponse;
    private ItemResponse itemResponse;

    public RequestServiceImplTest() {
        user = new User();
        user.setId(1L);
        user.setName("user name test");
        user.setEmail("user@test.com");

        userResponse = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user@test.com")
                .build();

        request = new Request();
        request.setId(1L);
        request.setDescription("request description test");
        request.setRequester(user);
        request.setCreationDate(LocalDateTime.now());

        requestCreateDto = RequestCreateDto.builder()
                .description("request description test")
                .build();

        requestResponse = RequestResponse.builder()
                .id(1L)
                .description("request description test")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .available(true)
                .requestId(1L)
                .build();

        requestWithAnswersResponse = RequestWithAnswersResponse.builder()
                .id(1L)
                .description("request description test")
                .created(LocalDateTime.now())
                .items(List.of(itemResponse))
                .build();
    }

    @Test
    void shouldCreateRequest() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestResponse actualResponse = requestService.createRequest(requestCreateDto, 1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("created")
                .isEqualTo(requestResponse);

        verify(userService, times(1)).getUser(1L);
        verify(requestRepository, times(1)).save(any(Request.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateRequest() {
        when(userService.getUser(anyLong())).thenThrow(new NotFoundException(""));

        assertThatThrownBy(() -> requestService.createRequest(requestCreateDto, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, times(1)).getUser(1L);
        verify(requestRepository, never()).save(any(Request.class));
    }

    @Test
    void shouldFindAllUserRequests() {
        when(requestRepository.findRequestsByRequester_IdOrderByCreationDateDesc(anyLong())).thenReturn(List.of(request));
        when(itemService.findItemsByRequestIds(anyList())).thenReturn(List.of(itemResponse));

        List<RequestWithAnswersResponse> actualResponses = requestService.findAllUserRequests(1L);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("created")
                .containsExactlyInAnyOrder(requestWithAnswersResponse);

        verify(requestRepository, times(1)).findRequestsByRequester_IdOrderByCreationDateDesc(1L);
        verify(itemService, times(1)).findItemsByRequestIds(List.of(1L));
    }

    @Test
    void shouldFindAllRequests() {
        when(requestRepository.findAllByOrderByCreationDateDesc()).thenReturn(List.of(request));

        List<RequestResponse> actualResponses = requestService.findAllRequests();

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("created")
                .containsExactlyInAnyOrder(requestResponse);

        verify(requestRepository, times(1)).findAllByOrderByCreationDateDesc();
    }

    @Test
    void shouldFindRequestById() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemService.findItemsByRequestIds(anyList())).thenReturn(List.of(itemResponse));

        RequestWithAnswersResponse actualResponse = requestService.findRequestById(1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("created")
                .isEqualTo(requestWithAnswersResponse);

        verify(requestRepository, times(1)).findById(1L);
        verify(itemService, times(1)).findItemsByRequestIds(List.of(1L));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestNotFound() {
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> requestService.findRequestById(1L))
                .isInstanceOf(NotFoundException.class);

        verify(requestRepository, times(1)).findById(1L);
        verify(itemService, never()).findItemsByRequestIds(anyList());
    }
}