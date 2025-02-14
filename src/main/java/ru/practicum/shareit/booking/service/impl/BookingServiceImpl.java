package ru.practicum.shareit.booking.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.InvalidDateException;
import ru.practicum.shareit.exception.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

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
