package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    MergeBookingResponse createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest,
                                       @RequestHeader(value = "X-Sharer-User-Id") Long bookerId) {
        return bookingService.createBooking(createBookingRequest, bookerId);
    }
}
