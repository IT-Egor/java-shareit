package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;

public interface BookingService {
    MergeBookingResponse createBooking(CreateBookingRequest createBookingRequest, Long bookerId);
}
