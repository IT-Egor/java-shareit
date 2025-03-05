package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;

import java.util.Collection;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest createBookingRequest, Long bookerId);

    BookingResponse setApproved(Long bookingId, Boolean approved, Long ownerId);

    BookingResponse getBooking(Long bookingId, Long userId);

    Collection<BookingResponse> getBookerBookings(Long bookerId, State state);

    Collection<BookingResponse> getOwnerBookings(Long ownerId, State state);
}
