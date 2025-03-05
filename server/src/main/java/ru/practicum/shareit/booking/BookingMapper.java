package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserResponse;

@Mapper
public interface BookingMapper {
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "createBookingRequest.start")
    @Mapping(target = "endDate", source = "createBookingRequest.end")
    Booking createRequestToBooking(CreateBookingRequest createBookingRequest, Item item, User booker);

    @Mapping(target = "item", source = "item")
    @Mapping(target = "booker", source = "booker")
    @Mapping(target = "start", source = "booking.startDate")
    @Mapping(target = "end", source = "booking.endDate")
    @Mapping(target = "id", source = "booking.id")
    BookingResponse bookingToResponse(Booking booking, ItemResponse item, UserResponse booker);
}
