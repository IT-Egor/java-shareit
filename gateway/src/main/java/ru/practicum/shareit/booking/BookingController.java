package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.dto.State;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(@Valid @RequestBody CreateBookingRequest createBookingRequest,
                                                @RequestHeader(value = "X-Sharer-User-Id") Long bookerId) {
        return bookingClient.createBooking(bookerId, createBookingRequest);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> setApproved(@PathVariable Long bookingId,
                                              @RequestParam Boolean approved,
                                              @RequestHeader(value = "X-Sharer-User-Id") Long ownerId) {
        return bookingClient.setApproved(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBooking(@PathVariable Long bookingId,
                                             @RequestHeader(value = "X-Sharer-User-Id") Long userId) {

        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getBookerBookings(@RequestHeader(value = "X-Sharer-User-Id") Long bookerId,
                                                    @RequestParam(required = false, defaultValue = "ALL") State state) {
        return bookingClient.getBookerBookings(bookerId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
                                                   @RequestParam(required = false, defaultValue = "ALL") State state) {
        return bookingClient.getOwnerBookings(ownerId, state);
    }
}
