package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.user.dto.UserResponse;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void testCreateBooking() throws Exception {
        CreateBookingRequest request = CreateBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .build();

        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user email test")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(request.getStart())
                .end(request.getEnd())
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingService.createBooking(Mockito.any(CreateBookingRequest.class), Mockito.eq(1L)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("item name test"))
                .andExpect(jsonPath("$.item.description").value("item description test"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("user name test"))
                .andExpect(jsonPath("$.booker.email").value("user email test"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void testSetApproved() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .build();

        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user email test")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.APPROVED)
                .build();

        Mockito.when(bookingService.setApproved(Mockito.eq(1L), Mockito.eq(true), Mockito.eq(1L)))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("item name test"))
                .andExpect(jsonPath("$.item.description").value("item description test"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("user name test"))
                .andExpect(jsonPath("$.booker.email").value("user email test"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void testGetBooking() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .build();

        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user email test")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .build();

        Mockito.when(bookingService.getBooking(Mockito.eq(1L), Mockito.eq(1L)))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("item name test"))
                .andExpect(jsonPath("$.item.description").value("item description test"))
                .andExpect(jsonPath("$.booker.id").value(1L))
                .andExpect(jsonPath("$.booker.name").value("user name test"))
                .andExpect(jsonPath("$.booker.email").value("user email test"))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void testGetBookerBookings() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .build();

        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user email test")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .build();

        List<BookingResponse> bookings = Collections.singletonList(response);

        Mockito.when(bookingService.getBookerBookings(Mockito.eq(1L), Mockito.eq(State.ALL)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("item name test"))
                .andExpect(jsonPath("$[0].item.description").value("item description test"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("user name test"))
                .andExpect(jsonPath("$[0].booker.email").value("user email test"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    void testGetOwnerBookings() throws Exception {
        ItemResponse item = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .build();

        UserResponse user = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user email test")
                .build();

        BookingResponse response = BookingResponse.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .build();

        List<BookingResponse> bookings = Collections.singletonList(response);

        Mockito.when(bookingService.getOwnerBookings(Mockito.eq(1L), Mockito.eq(State.ALL)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("item name test"))
                .andExpect(jsonPath("$[0].item.description").value("item description test"))
                .andExpect(jsonPath("$[0].booker.id").value(1L))
                .andExpect(jsonPath("$[0].booker.name").value("user name test"))
                .andExpect(jsonPath("$[0].booker.email").value("user email test"))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }
}