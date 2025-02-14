package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;

import java.util.Collection;

public interface BookingService {
    MergeBookingResponse createBooking(CreateBookingRequest createBookingRequest, Long bookerId);

    MergeBookingResponse setApproved(Long bookingId, Boolean approved, Long ownerId);

    BookingResponse getBooking(Long bookingId, Long userId);

    Collection<BookingResponse> getUserBookings(Long bookerId, State state);
}
