package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.impl.ItemServiceImpl;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private UserService userService;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private UserResponse userResponse;
    private Item item;
    private CreateItemRequest createItemRequest;
    private UpdateItemRequest updateItemRequest;
    private Comment comment;
    private CreateCommentRequest createCommentRequest;

    public ItemServiceImplTest() {
        user = new User();
        user.setId(1L);
        user.setName("user name test");
        user.setEmail("user@test.com");

        userResponse = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user@test.com")
                .build();

        item = new Item();
        item.setId(1L);
        item.setName("item name test");
        item.setDescription("item description test");
        item.setAvailable(true);
        item.setOwner(user);

        createItemRequest = CreateItemRequest.builder()
                .name("item name test")
                .description("item description test")
                .available(true)
                .requestId(null)
                .build();

        updateItemRequest = UpdateItemRequest.builder()
                .name("updated item name test")
                .description("updated item description test")
                .available(false)
                .build();

        comment = new Comment();
        comment.setId(1L);
        comment.setText("comment text test");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());

        createCommentRequest = CreateCommentRequest.builder()
                .text("comment text test")
                .build();
    }

    @Test
    void shouldCreateItem() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemResponse expectedResponse = itemMapper.itemToResponse(item);
        ItemResponse actualResponse = itemService.createItem(createItemRequest, 1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(userService, times(1)).getUser(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldCreateItemWithRequest() {
        Request request = new Request();
        request.setId(1L);
        request.setDescription("request description test");
        request.setRequester(user);
        request.setCreationDate(LocalDateTime.now());

        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        CreateItemRequest createItemRequestWithRequest = CreateItemRequest.builder()
                .name("item name test")
                .description("item description test")
                .available(true)
                .requestId(1L)
                .build();

        ItemResponse expectedResponse = itemMapper.itemToResponse(item);
        ItemResponse actualResponse = itemService.createItem(createItemRequestWithRequest, 1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(userService, times(1)).getUser(1L);
        verify(requestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenRequestNotFoundForCreateItem() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());

        CreateItemRequest createItemRequestWithRequest = CreateItemRequest.builder()
                .name("item name test")
                .description("item description test")
                .available(true)
                .requestId(1L)
                .build();

        assertThatThrownBy(() -> itemService.createItem(createItemRequestWithRequest, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, times(1)).getUser(1L);
        verify(requestRepository, times(1)).findById(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateItem() {
        when(userService.getUser(anyLong())).thenThrow(new NotFoundException(""));

        assertThatThrownBy(() -> itemService.createItem(createItemRequest, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, times(1)).getUser(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldUpdateItem() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> {
            Item savedItem = invocation.getArgument(0);
            savedItem.setName(updateItemRequest.getName());
            savedItem.setDescription(updateItemRequest.getDescription());
            savedItem.setAvailable(updateItemRequest.getAvailable());
            return savedItem;
        });

        ItemResponse actualResponse = itemService.updateItem(1L, updateItemRequest, 1L);

        ItemResponse expectedResponse = ItemResponse.builder()
                .id(1L)
                .name("updated item name test")
                .description("updated item description test")
                .available(false)
                .ownerId(1L)
                .requestId(null)
                .build();

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundForUpdateItem() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.updateItem(1L, updateItemRequest, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldFindItem() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ItemResponse expectedResponse = itemMapper.itemToResponse(item);
        ItemResponse actualResponse = itemService.findItem(1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findItem(1L))
                .isInstanceOf(NotFoundException.class);

        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void shouldFindItemWithComments() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItem_Id(anyLong())).thenReturn(List.of(comment));

        ItemResponseComments expectedResponse = itemMapper.itemToResponseComments(item, List.of(commentMapper.commentToResponse(comment)));
        ItemResponseComments actualResponse = itemService.findItemWithComments(1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(itemRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findAllByItem_Id(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundForFindItemWithComments() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.findItemWithComments(1L))
                .isInstanceOf(NotFoundException.class);

        verify(itemRepository, times(1)).findById(1L);
        verify(commentRepository, never()).findAllByItem_Id(anyLong());
    }

    @Test
    void shouldGetAllUserItems() {
        when(itemRepository.findItemsByOwnerId(anyLong())).thenReturn(List.of(item));
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(anyLong())).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItem_Owner_Id(anyLong())).thenReturn(Collections.emptyList());

        List<ItemResponseBookingComments> expectedResponses = List.of(
                itemMapper.itemToResponseBookingComments(item, null, null, Collections.emptyList())
        );
        List<ItemResponseBookingComments> actualResponses = itemService.getAllUserItems(1L);

        assertThat(actualResponses).isEqualTo(expectedResponses);

        verify(itemRepository, times(1)).findItemsByOwnerId(1L);
        verify(bookingRepository, times(1)).findAllByItem_Owner_IdOrderByStartDateDesc(1L);
        verify(commentRepository, times(1)).findAllByItem_Owner_Id(1L);
    }

    @Test
    void shouldSearchItems() {
        when(itemRepository.findItemsByNameLikeIgnoreCaseAndAvailableTrue(anyString())).thenReturn(List.of(item));

        List<ItemResponse> expectedResponses = List.of(itemMapper.itemToResponse(item));
        List<ItemResponse> actualResponses = itemService.searchItems("item");

        assertThat(actualResponses).isEqualTo(expectedResponses);

        verify(itemRepository, times(1)).findItemsByNameLikeIgnoreCaseAndAvailableTrue("item");
    }

    @Test
    void shouldAddComment() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class))
        ).thenReturn(List.of(new Booking()));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        MergeCommentResponse expectedResponse = commentMapper.commentToMergeResponse(
                comment, itemMapper.itemToResponse(item), user.getName()
        );
        MergeCommentResponse actualResponse = itemService.addComment(createCommentRequest, 1L, 1L);

        assertThat(actualResponse).isEqualTo(expectedResponse);

        verify(userService, times(1)).getUser(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdAndEndDateBeforeOrderByStartDateDesc(eq(1L), eq(1L), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundForAddComment() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.addComment(createCommentRequest, 1L, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, never()).getUser(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, never())
                .findByItem_IdAndBooker_IdAndEndDateBeforeOrderByStartDateDesc(anyLong(), anyLong(), any(LocalDateTime.class));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenAddingCommentWithoutBooking() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndBooker_IdAndEndDateBeforeOrderByStartDateDesc(
                anyLong(),
                anyLong(),
                any(LocalDateTime.class))
        ).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.addComment(createCommentRequest, 1L, 1L))
                .isInstanceOf(ValidationException.class);

        verify(userService, times(1)).getUser(1L);
        verify(itemRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1))
                .findByItem_IdAndBooker_IdAndEndDateBeforeOrderByStartDateDesc(eq(1L), eq(1L), any(LocalDateTime.class));
    }

    @Test
    void shouldFindItemsByRequestIds() {
        when(itemRepository.findItemsByRequest_IdIn(anyList())).thenReturn(List.of(item));

        List<ItemResponse> expectedResponses = List.of(itemMapper.itemToResponse(item));
        List<ItemResponse> actualResponses = itemService.findItemsByRequestIds(List.of(1L));

        assertThat(actualResponses).isEqualTo(expectedResponses);

        verify(itemRepository, times(1)).findItemsByRequest_IdIn(List.of(1L));
    }
}