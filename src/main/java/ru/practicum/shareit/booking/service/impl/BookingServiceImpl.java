package ru.practicum.shareit.booking.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.InvalidDateException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Override
    public MergeBookingResponse createBooking(CreateBookingRequest createBookingRequest, Long bookerId) {
        User booker = userMapper.responseToUser(userService.getUser(bookerId));
        Item item = getItem(createBookingRequest.getItemId());

        Booking booking = bookingMapper.createRequestToBooking(createBookingRequest, item, booker);
        booking.setStatus(Status.WAITING);
        validateBooking(booking);

        return bookingMapper.bookingToMergeResponse(
                bookingRepository.save(booking),
                itemMapper.itemToResponse(item),
                userMapper.userToResponse(booker));
    }

    @Override
    public MergeBookingResponse setApproved(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = getBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AuthorizationException(String.format("User %d is not owner of item %d", ownerId, bookingId));
        }
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.bookingToMergeResponse(
                bookingRepository.save(booking),
                itemMapper.itemToResponse(booking.getItem()),
                userMapper.userToResponse(booking.getBooker()));
    }

    public BookingResponse getBookingResponse(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            return bookingMapper.bookingToResponse(bookingOpt.get());
        } else {
            throw new NotFoundException(String.format("Booking with id %d not found", bookingId));
        }
    }

    private Booking getBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            return bookingOpt.get();
        } else {
            throw new NotFoundException(String.format("Booking with id %d not found", bookingId));
        }
    }

    private void validateBooking(Booking booking) {
        LocalDateTime start = booking.getStartDate();
        LocalDateTime end = booking.getEndDate();

        if (start.isAfter(end)) {
            throw new InvalidDateException("Start date is after end date");
        } else if (start.equals(end)) {
            throw new InvalidDateException("Start date equals end date");
        } else if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new UnavailableItemBookingException(String.format("Item %d is not available", booking.getItem().getId()));
        }
    }

    private Item getItem(Long itemId) {
        ItemResponse itemResponse = itemService.getItem(itemId);
        User owner = userMapper.responseToUser(userService.getUser(itemResponse.getOwnerId()));
        return itemMapper.responseToItem(itemResponse, owner);
    }
}
