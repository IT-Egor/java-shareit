package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.CreateUserRequest;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=shareit",
        "spring.datasource.password=shareit",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private UserResponse userResponse;
    private UserResponse ownerResponse;
    private ItemResponse itemResponse;

    @BeforeEach
    void setUp() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .name("test user")
                .email("test@example.com")
                .build();
        userResponse = userService.createUser(createUserRequest);

        CreateUserRequest createOwnerRequest = CreateUserRequest.builder()
                .name("test owner")
                .email("owner@example.com")
                .build();
        ownerResponse = userService.createUser(createOwnerRequest);

        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("test item")
                .description("item description")
                .available(true)
                .build();
        itemResponse = itemService.createItem(createItemRequest, ownerResponse.getId());
    }

    @Test
    void shouldCreateBooking() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingResponse bookingResponse = bookingService.createBooking(createBookingRequest, userResponse.getId());

        assertThat(bookingResponse.getId()).isNotNull();
        assertThat(bookingResponse.getStatus()).isEqualTo(Status.WAITING);
        assertThat(bookingResponse.getItem().getId()).isEqualTo(itemResponse.getId());
        assertThat(bookingResponse.getBooker().getId()).isEqualTo(userResponse.getId());

        Booking savedBooking = bookingRepository.findById(bookingResponse.getId()).orElseThrow();
        assertThat(savedBooking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(savedBooking.getItem().getId()).isEqualTo(itemResponse.getId());
        assertThat(savedBooking.getBooker().getId()).isEqualTo(userResponse.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateBooking() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundForCreateBooking() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(999L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, userResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowUnavailableItemBookingExceptionWhenItemNotAvailable() {
        CreateItemRequest createItemRequest = CreateItemRequest.builder()
                .name("test item")
                .description("item description")
                .available(false)
                .build();
        ItemResponse unavailableItemResponse = itemService.createItem(createItemRequest, ownerResponse.getId());

        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(unavailableItemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, userResponse.getId()))
                .isInstanceOf(UnavailableItemBookingException.class);
    }

    @Test
    void shouldSetApproved() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponse bookingResponse = bookingService.createBooking(createBookingRequest, userResponse.getId());

        BookingResponse approvedBookingResponse = bookingService.setApproved(bookingResponse.getId(), true, ownerResponse.getId());

        assertThat(approvedBookingResponse.getStatus()).isEqualTo(Status.APPROVED);

        Booking savedBooking = bookingRepository.findById(bookingResponse.getId()).orElseThrow();
        assertThat(savedBooking.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenUserIsNotOwnerForSetApproved() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponse bookingResponse = bookingService.createBooking(createBookingRequest, userResponse.getId());

        assertThatThrownBy(() -> bookingService.setApproved(bookingResponse.getId(), true, userResponse.getId()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void shouldGetBooking() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponse bookingResponse = bookingService.createBooking(createBookingRequest, userResponse.getId());

        BookingResponse foundBooking = bookingService.getBooking(bookingResponse.getId(), userResponse.getId());

        assertThat(foundBooking.getId()).isEqualTo(bookingResponse.getId());
        assertThat(foundBooking.getStatus()).isEqualTo(Status.WAITING);
        assertThat(foundBooking.getItem().getId()).isEqualTo(itemResponse.getId());
        assertThat(foundBooking.getBooker().getId()).isEqualTo(userResponse.getId());
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenUserIsNotBookerOrOwnerForGetBooking() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        BookingResponse bookingResponse = bookingService.createBooking(createBookingRequest, userResponse.getId());

        assertThatThrownBy(() -> bookingService.getBooking(bookingResponse.getId(), 999L))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void shouldGetBookerBookings() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(createBookingRequest, userResponse.getId());

        Collection<BookingResponse> bookings = bookingService.getBookerBookings(userResponse.getId(), State.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getItem().getId()).isEqualTo(itemResponse.getId());
        assertThat(bookings.iterator().next().getBooker().getId()).isEqualTo(userResponse.getId());
    }

    @Test
    void shouldGetOwnerBookings() {
        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(itemResponse.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingService.createBooking(createBookingRequest, userResponse.getId());

        Collection<BookingResponse> bookings = bookingService.getOwnerBookings(ownerResponse.getId(), State.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getItem().getId()).isEqualTo(itemResponse.getId());
        assertThat(bookings.iterator().next().getBooker().getId()).isEqualTo(userResponse.getId());
    }
}