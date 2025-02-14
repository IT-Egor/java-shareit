package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.MergeBookingResponse;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

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

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    MergeBookingResponse setApproved(@PathVariable Long bookingId,
                                     @RequestParam Boolean approved,
                                     @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return bookingService.setApproved(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    BookingResponse getBooking(@PathVariable Long bookingId,
                               @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    Collection<BookingResponse> getUserBookings(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state) {
        return bookingService.getUserBookings(bookerId, state);
    }
}
