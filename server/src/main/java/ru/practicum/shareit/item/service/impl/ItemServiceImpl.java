package ru.practicum.shareit.item.service.impl;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemResponse createItem(CreateItemRequest createItemRequest, Long ownerId) {
        User user = userMapper.responseToUser(userService.getUser(ownerId));
        Item item;

        if (createItemRequest.getRequestId().isPresent()) {
            Request request = requestRepository.findById(createItemRequest.getRequestId().get()).orElseThrow(
                    () -> new NotFoundException("Request not found")
            );
            item = itemMapper.createRequestToItem(createItemRequest, user, request);
        } else {
            item = itemMapper.createRequestToItem(createItemRequest, user, null);
        }

        return itemMapper.itemToResponse(itemRepository.save(item));
    }

    @Override
    public ItemResponse updateItem(Long itemId, UpdateItemRequest updateItemRequest, Long ownerId) {
        User owner = userMapper.responseToUser(userService.getUser(ownerId));
        Item item = itemMapper.updateRequestToItem(updateItemRequest, owner, itemId);

        Item oldItem = getUpdatedOldItem(item);
        itemRepository.save(oldItem);

        return itemMapper.itemToResponse(item);
    }

    @Override
    public ItemResponse findItem(Long itemId) {
        return itemMapper.itemToResponse(getItem(itemId));
    }

    @Override
    public ItemResponseComments findItemWithComments(Long itemId) {
        Item item = getItem(itemId);
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        List<ItemCommentResponse> itemCommentResponses = comments.stream().map(commentMapper::commentToResponse).toList();
        return itemMapper.itemToResponseComments(item, itemCommentResponses);
    }

    @Override
    public List<ItemResponseBookingComments> getAllUserItems(Long ownerId) {
        List<Item> items = itemRepository.findItemsByOwnerId(ownerId);
        List<Booking> bookings = new ArrayList<>(bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(ownerId));
        List<Comment> comments = commentRepository.findAllByItem_Owner_Id(ownerId);

        Map<Long, List<Booking>> bookingsByItemId = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        Map<Long, List<Comment>> commentsByItemId = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        LocalDateTime now = LocalDateTime.now();
        return items.stream()
                .map(item -> {
                    List<Booking> itemBookings = bookingsByItemId.getOrDefault(item.getId(), List.of());
                    LocalDateTime nextBooking = itemBookings.stream()
                            .map(Booking::getStartDate)
                            .filter(date -> date.isAfter(now))
                            .min(LocalDateTime::compareTo)
                            .orElse(null);
                    LocalDateTime lastBooking = itemBookings.stream()
                            .map(Booking::getStartDate)
                            .filter(date -> date.isBefore(now))
                            .max(LocalDateTime::compareTo)
                            .orElse(null);
                    List<ItemCommentResponse> itemComments = commentsByItemId.getOrDefault(item.getId(), List.of())
                            .stream()
                            .map(commentMapper::commentToResponse)
                            .toList();
                    return itemMapper.itemToResponseBookingComments(item, nextBooking, lastBooking, itemComments);
                }).toList();
    }

    @Override
    public List<ItemResponse> findItemsByRequestIds(List<Long> requestIds) {
        return itemRepository.findItemsByRequest_IdIn(requestIds).stream()
                .map(itemMapper::itemToResponse)
                .toList();
    }

    @Override
    public List<ItemResponse> searchItems(String query) {
        return itemRepository.findItemsByNameLikeIgnoreCaseAndAvailableTrue(query).stream()
                .map(itemMapper::itemToResponse).toList();
    }

    @Override
    public MergeCommentResponse addComment(CreateCommentRequest createCommentRequest, Long itemId, Long authorId) {
        Item item = getItem(itemId);
        User author = userMapper.responseToUser(userService.getUser(authorId));

        if (bookingRepository.findPastByItem_IdAndBooker_Id(itemId, authorId).isEmpty()) {
            throw new ValidationException(String.format("Item id=%d completed booking of user id=%d not found", itemId, authorId));
        }

        Comment comment = commentMapper.createRequestToComment(createCommentRequest, item, author);
        comment.setCreationDate(LocalDateTime.now());
        return commentMapper.commentToMergeResponse(
                commentRepository.save(comment),
                itemMapper.itemToResponse(item),
                author.getName());
    }

    private Item getItem(Long itemId) {
        Optional<Item> itemOpt = itemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            return itemOpt.get();
        } else {
            throw new NotFoundException(String.format("Item with id %s not found", itemId));
        }
    }

    private Item getUpdatedOldItem(Item item) {
        Item oldItem = getItem(item.getId());
        if (!item.getOwner().getId()
                .equals(oldItem.getOwner().getId())) {
            throw new AuthorizationException("Authorization failed");
        }
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        return oldItem;
    }
}
