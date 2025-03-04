package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestResponse;
import ru.practicum.shareit.request.dto.RequestWithAnswersResponse;
import ru.practicum.shareit.request.service.impl.RequestServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=shareit",
        "spring.datasource.password=shareit",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestServiceImpl requestService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RequestRepository requestRepository;

    private UserResponse userResponse;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("test user")
                .email("test@example.com")
                .build();
        userResponse = userService.createUser(createUserRequest);

        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("test item")
                .description("item description")
                .available(true)
                .build();
        itemResponse = itemService.createItem(createItemRequest, userResponse.getId());
    }

    @Test
    void shouldCreateRequest() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();

        RequestResponse requestResponse = requestService.createRequest(requestCreateDto, userResponse.getId());

        assertThat(requestResponse.getId()).isNotNull();
        assertThat(requestResponse.getDescription()).isEqualTo("test description");
        assertThat(requestResponse.getRequesterId()).isEqualTo(userResponse.getId());

        Request savedRequest = requestRepository.findById(requestResponse.getId()).orElseThrow();
        assertThat(savedRequest.getDescription()).isEqualTo("test description");
        assertThat(savedRequest.getRequester().getId()).isEqualTo(userResponse.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateRequest() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();

        assertThatThrownBy(() -> requestService.createRequest(requestCreateDto, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllUserRequests() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();
        RequestResponse requestResponse = requestService.createRequest(requestCreateDto, userResponse.getId());

        List<RequestWithAnswersResponse> userRequests = requestService.findAllUserRequests(userResponse.getId());

        assertThat(userRequests).hasSize(1);
        assertThat(userRequests.get(0).getId()).isEqualTo(requestResponse.getId());
        assertThat(userRequests.get(0).getDescription()).isEqualTo("test description");
    }

    @Test
    void shouldFindAllRequests() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();
        RequestResponse requestResponse = requestService.createRequest(requestCreateDto, userResponse.getId());

        List<RequestResponse> allRequests = requestService.findAllRequests();

        assertThat(allRequests).hasSize(1);
        assertThat(allRequests.get(0).getId()).isEqualTo(requestResponse.getId());
        assertThat(allRequests.get(0).getDescription()).isEqualTo("test description");
        assertThat(allRequests.get(0).getRequesterId()).isEqualTo(userResponse.getId());
    }

    @Test
    void shouldFindRequestById() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();
        RequestResponse requestResponse = requestService.createRequest(requestCreateDto, userResponse.getId());

        RequestWithAnswersResponse foundRequest = requestService.findRequestById(requestResponse.getId());

        assertThat(foundRequest.getId()).isEqualTo(requestResponse.getId());
        assertThat(foundRequest.getId()).isEqualTo(requestResponse.getId());
        assertThat(foundRequest.getDescription()).isEqualTo("test description");
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestNotFound() {
        assertThatThrownBy(() -> requestService.findRequestById(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindRequestWithItems() {
        RequestCreateDto requestCreateDto = RequestCreateDto.builder()
                .description("test description")
                .build();
        RequestResponse requestResponse = requestService.createRequest(requestCreateDto, userResponse.getId());

        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("test item")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();
        itemResponse = itemService.createItem(createItemRequest, userResponse.getId());

        RequestWithAnswersResponse foundRequest = requestService.findRequestById(requestResponse.getId());

        assertThat(foundRequest.getItems()).hasSize(1);
        assertThat(foundRequest.getId()).isEqualTo(requestResponse.getId());
        assertThat(foundRequest.getItems().get(0).getId()).isEqualTo(itemResponse.getId());
        assertThat(foundRequest.getItems().get(0).getName()).isEqualTo("test item");
    }
}