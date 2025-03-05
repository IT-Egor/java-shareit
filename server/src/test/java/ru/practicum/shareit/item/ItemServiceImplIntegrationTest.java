package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingServiceImpl bookingServiceImpl;

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
    void shouldCreateItem() {
        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("new item")
                .description("new item description")
                .available(true)
                .build();

        ItemResponse newItemResponse = itemService.createItem(createItemRequest, userResponse.getId());

        assertThat(newItemResponse.getId()).isNotNull();
        assertThat(newItemResponse.getName()).isEqualTo("new item");
        assertThat(newItemResponse.getDescription()).isEqualTo("new item description");
        assertThat(newItemResponse.getAvailable()).isTrue();

        Item savedItem = itemRepository.findById(newItemResponse.getId()).orElseThrow();
        assertThat(savedItem.getName()).isEqualTo("new item");
        assertThat(savedItem.getDescription()).isEqualTo("new item description");
        assertThat(savedItem.getAvailable()).isTrue();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateItem() {
        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("new item")
                .description("new item description")
                .available(true)
                .build();

        assertThatThrownBy(() -> itemService.createItem(createItemRequest, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldUpdateItem() {
        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .name("updated item")
                .description("updated item description")
                .available(false)
                .build();

        ItemResponse updatedItemResponse = itemService.updateItem(itemResponse.getId(), updateItemRequest, userResponse.getId());

        assertThat(updatedItemResponse.getId()).isEqualTo(itemResponse.getId());
        assertThat(updatedItemResponse.getName()).isEqualTo("updated item");
        assertThat(updatedItemResponse.getDescription()).isEqualTo("updated item description");
        assertThat(updatedItemResponse.getAvailable()).isFalse();
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenUpdatingItemWithWrongOwner() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("test user")
                .email("test2@example.com")
                .build();
        userResponse = userService.createUser(createUserRequest);

        UpdateItemRequest updateItemRequest = UpdateItemRequest.builder()
                .name("updated item")
                .description("updated item description")
                .available(false)
                .build();

        assertThatThrownBy(() -> itemService.updateItem(itemResponse.getId(), updateItemRequest, userResponse.getId()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void shouldFindItemById() {
        ItemResponse foundItem = itemService.findItem(itemResponse.getId());

        assertThat(foundItem.getId()).isEqualTo(itemResponse.getId());
        assertThat(foundItem.getName()).isEqualTo("test item");
        assertThat(foundItem.getDescription()).isEqualTo("item description");
        assertThat(foundItem.getAvailable()).isTrue();
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        assertThatThrownBy(() -> itemService.findItem(999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldFindAllUserItems() {
        List<ItemResponseBookingComments> userItems = itemService.getAllUserItems(userResponse.getId());

        assertThat(userItems).hasSize(1);
        assertThat(userItems.get(0).getName()).isEqualTo("test item");
        assertThat(userItems.get(0).getDescription()).isEqualTo("item description");
        assertThat(userItems.get(0).getAvailable()).isTrue();
    }

    @Test
    void shouldSearchItems() {
        List<ItemResponse> foundItems = itemService.searchItems("test");

        assertThat(foundItems).hasSize(1);
        assertThat(foundItems.get(0).getName()).isEqualTo("test item");
        assertThat(foundItems.get(0).getDescription()).isEqualTo("item description");
        assertThat(foundItems.get(0).getAvailable()).isTrue();
    }

    @Test
    void shouldAddComment() {
        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .text("test comment")
                .build();

        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .build();

        bookingServiceImpl.createBooking(createBookingRequest, userResponse.getId());

        MergeCommentResponse commentResponse = itemService.addComment(createCommentRequest, itemResponse.getId(), userResponse.getId());

        assertThat(commentResponse.getText()).isEqualTo("test comment");
        assertThat(commentResponse.getAuthorName()).isEqualTo("test user");
        assertThat(commentResponse.getItem().getName()).isEqualTo("test item");
    }

    @Test
    void shouldThrowValidationExceptionWhenAddingCommentWithoutBooking() {
        CreateCommentRequest createCommentRequest = CreateCommentRequest.builder()
                .text("test comment")
                .build();

        assertThatThrownBy(() -> itemService.addComment(createCommentRequest, itemResponse.getId(), userResponse.getId()))
                .isInstanceOf(ValidationException.class);
    }
}