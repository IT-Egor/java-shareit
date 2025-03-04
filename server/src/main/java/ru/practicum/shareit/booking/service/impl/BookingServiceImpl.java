package ru.practicum.shareit.booking.service.impl;

import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
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
import java.util.Collection;
import java.util.List;
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
    public BookingResponse createBooking(CreateBookingRequest createBookingRequest, Long bookerId) {
        User booker = userMapper.responseToUser(userService.getUser(bookerId));
        Item item = getItem(createBookingRequest.getItemId());

        Booking booking = bookingMapper.createRequestToBooking(createBookingRequest, item, booker);
        booking.setStatus(Status.WAITING);
        validateBooking(booking);

        return bookingMapper.bookingToResponse(
                bookingRepository.save(booking),
                itemMapper.itemToResponse(item),
                userMapper.userToResponse(booker));
    }

    @Override
    public BookingResponse setApproved(Long bookingId, Boolean approved, Long ownerId) {
        Booking booking = findBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AuthorizationException(String.format("User id=%d is not owner of item id=%d", ownerId, bookingId));
        }
        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return bookingMapper.bookingToResponse(
                bookingRepository.save(booking),
                itemMapper.itemToResponse(booking.getItem()),
                userMapper.userToResponse(booking.getBooker()));
    }

    @Override
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = findBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AuthorizationException(String.format("User id=%d doesnt have access to item id=%d", userId, bookingId));
        }

        return bookingMapper.bookingToResponse(
                booking,
                itemMapper.itemToResponse(booking.getItem()),
                userMapper.userToResponse(booking.getBooker()));
    }

    @Override
    public Collection<BookingResponse> getBookerBookings(Long bookerId, State state) {
        userService.getUser(bookerId);
        Collection<Booking> bookings = List.of();
        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByBooker_IdOrderByStartDateDesc(bookerId);
            case CURRENT -> bookings = bookingRepository.findByBooker_IdAndStartDateBeforeAndEndDateAfterOrderByStartDateDesc(bookerId);
            case PAST -> bookings = bookingRepository.findPastByBooker_Id(bookerId);
            case FUTURE -> bookings = bookingRepository.findFutureByBooker_Id(bookerId);
            case WAITING -> bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(bookerId, Status.REJECTED);
        }

        return bookings.stream().map(booking ->
                bookingMapper.bookingToResponse(
                        booking,
                        itemMapper.itemToResponse(booking.getItem()),
                        userMapper.userToResponse(booking.getBooker()))
        ).toList();
    }

    @Override
    public Collection<BookingResponse> getOwnerBookings(Long ownerId, State state) {
        userService.getUser((ownerId));
        Collection<Booking> bookings = List.of();
        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(ownerId);
            case CURRENT -> bookings = bookingRepository.findCurrentByOwner_Id(ownerId);
            case PAST -> bookings = bookingRepository.findPastByOwner_Id(ownerId);
            case FUTURE -> bookings = bookingRepository.findFutureByOwner_Id(ownerId);
            case WAITING -> bookings = bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, Status.WAITING);
            case REJECTED -> bookings = bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(ownerId, Status.REJECTED);
        }

        return bookings.stream().map(booking ->
                bookingMapper.bookingToResponse(
                        booking,
                        itemMapper.itemToResponse(booking.getItem()),
                        userMapper.userToResponse(booking.getBooker()))
        ).toList();
    }

    private Booking findBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            return bookingOpt.get();
        } else {
            throw new NotFoundException(String.format("Booking id=%d not found", bookingId));
        }
    }

    private void validateBooking(Booking booking) {
        LocalDateTime start = booking.getStartDate();
        LocalDateTime end = booking.getEndDate();

        if (start.isAfter(end)) {
            throw new ValidationException("Start date is after end date");
        } else if (start.equals(end)) {
            throw new ValidationException("Start date equals end date");
        } else if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new UnavailableItemBookingException(String.format("Item %d is not available", booking.getItem().getId()));
        }
    }

    private Item getItem(Long itemId) {
        ItemResponse itemResponse = itemService.findItem(itemId);
        User owner = userMapper.responseToUser(userService.getUser(itemResponse.getOwnerId()));
        return itemMapper.responseToItem(itemResponse, owner);
    }
}
