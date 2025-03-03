package ru.practicum.shareit.booking;

import jakarta.validation.ValidationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequest;
import ru.practicum.shareit.booking.service.impl.BookingServiceImpl;
import ru.practicum.shareit.exception.exceptions.AuthorizationException;
import ru.practicum.shareit.exception.exceptions.NotFoundException;
import ru.practicum.shareit.exception.exceptions.UnavailableItemBookingException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserResponse;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private UserResponse userResponse;
    private Item item;
    private ItemResponse itemResponse;
    private Booking booking;
    private CreateBookingRequest createBookingRequest;
    private BookingResponse bookingResponse;

    public BookingServiceImplTest() {
        user = new User();
        user.setId(1L);
        user.setName("user name test");
        user.setEmail("user@test.com");

        userResponse = UserResponse.builder()
                .id(1L)
                .name("user name test")
                .email("user@test.com")
                .build();

        item = new Item();
        item.setId(1L);
        item.setName("item name test");
        item.setDescription("item description test");
        item.setAvailable(true);
        item.setOwner(user);

        itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .available(true)
                .ownerId(1L)
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStartDate(LocalDateTime.now().plusDays(1));
        booking.setEndDate(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);

        createBookingRequest = CreateBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingResponse = BookingResponse.builder()
                .id(1L)
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(Status.WAITING)
                .item(itemResponse)
                .booker(userResponse)
                .build();
    }

    @Test
    void shouldCreateBooking() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemService.findItem(anyLong())).thenReturn(itemResponse);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse actualResponse = bookingService.createBooking(createBookingRequest, 1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("start", "end")
                .isEqualTo(bookingResponse);

        verify(userService, times(2)).getUser(1L);
        verify(itemService, times(1)).findItem(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundForCreateBooking() {
        when(userService.getUser(anyLong())).thenThrow(new NotFoundException(""));

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, times(1)).getUser(1L);
        verify(itemService, never()).findItem(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFoundForCreateBooking() {
        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemService.findItem(anyLong())).thenThrow(new NotFoundException(""));

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(userService, times(1)).getUser(1L);
        verify(itemService, times(1)).findItem(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldThrowUnavailableItemBookingExceptionWhenItemNotAvailable() {
        itemResponse = ItemResponse.builder()
                .id(1L)
                .name("item name test")
                .description("item description test")
                .available(false)
                .ownerId(1L)
                .build();

        when(userService.getUser(anyLong())).thenReturn(userResponse);
        when(itemService.findItem(anyLong())).thenReturn(itemResponse);

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 1L))
                .isInstanceOf(UnavailableItemBookingException.class);

        verify(userService, times(2)).getUser(1L);
        verify(itemService, times(1)).findItem(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldSetApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse expectedResponse = BookingResponse.builder()
                .id(1L)
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(Status.APPROVED)
                .item(itemResponse)
                .booker(userResponse)
                .build();

        BookingResponse actualResponse = bookingService.setApproved(1L, true, 1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("start", "end")
                .isEqualTo(expectedResponse);

        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldSetRejected() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse expectedResponse = BookingResponse.builder()
                .id(1L)
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .status(Status.REJECTED)
                .item(itemResponse)
                .booker(userResponse)
                .build();

        BookingResponse actualResponse = bookingService.setApproved(1L, false, 1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("start", "end")
                .isEqualTo(expectedResponse);

        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenUserIsNotOwnerForSetApproved() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.setApproved(1L, true, 2L))
                .isInstanceOf(AuthorizationException.class);

        verify(bookingRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingResponse actualResponse = bookingService.getBooking(1L, 1L);

        Assertions.assertThat(actualResponse)
                .usingRecursiveComparison()
                .ignoringFields("start", "end")
                .isEqualTo(bookingResponse);

        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowAuthorizationExceptionWhenUserIsNotBookerOrOwnerForGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBooking(1L, 2L))
                .isInstanceOf(AuthorizationException.class);

        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFoundForGetBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.getBooking(1L, 1L))
                .isInstanceOf(NotFoundException.class);

        verify(bookingRepository, times(1)).findById(1L);
    }

    @Test
    void shouldGetBookerBookingsAll() {
        when(bookingRepository.findAllByBooker_IdOrderByStartDateDesc(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.ALL);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByBooker_IdOrderByStartDateDesc(1L);
    }

    @Test
    void shouldGetBookerBookingsCurrent() {
        when(bookingRepository.findCurrentByBooker_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.CURRENT);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findCurrentByBooker_Id(1L);
    }

    @Test
    void shouldGetBookerBookingsPast() {
        when(bookingRepository.findPastByBooker_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.PAST);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findPastByBooker_Id(1L);
    }

    @Test
    void shouldGetBookerBookingsFuture() {
        when(bookingRepository.findFutureByBooker_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.FUTURE);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findFutureByBooker_Id(1L);
    }

    @Test
    void shouldGetBookerBookingsWaiting() {
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.WAITING))).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.WAITING);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByBooker_IdAndStatusOrderByStartDateDesc(1L, Status.WAITING);
    }

    @Test
    void shouldGetBookerBookingsRejected() {
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.REJECTED))).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getBookerBookings(1L, State.REJECTED);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByBooker_IdAndStatusOrderByStartDateDesc(1L, Status.REJECTED);
    }

    @Test
    void shouldGetOwnerBookingsAll() {
        when(bookingRepository.findAllByItem_Owner_IdOrderByStartDateDesc(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.ALL);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByItem_Owner_IdOrderByStartDateDesc(1L);
    }

    @Test
    void shouldGetOwnerBookingsCurrent() {
        when(bookingRepository.findCurrentByOwner_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.CURRENT);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findCurrentByOwner_Id(1L);
    }

    @Test
    void shouldGetOwnerBookingsPast() {
        when(bookingRepository.findPastByOwner_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.PAST);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findPastByOwner_Id(1L);
    }

    @Test
    void shouldGetOwnerBookingsFuture() {
        when(bookingRepository.findFutureByOwner_Id(anyLong())).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.FUTURE);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findFutureByOwner_Id(1L);
    }

    @Test
    void shouldGetOwnerBookingsWaiting() {
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.WAITING))).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.WAITING);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(1L, Status.WAITING);
    }

    @Test
    void shouldGetOwnerBookingsRejected() {
        when(bookingRepository.findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(anyLong(), eq(Status.REJECTED))).thenReturn(List.of(booking));

        Collection<BookingResponse> actualResponses = bookingService.getOwnerBookings(1L, State.REJECTED);

        Assertions.assertThat(actualResponses)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("start", "end")
                .containsExactlyInAnyOrder(bookingResponse);

        verify(bookingRepository, times(1)).findAllByItem_Owner_IdAndStatusOrderByStartDateDesc(1L, Status.REJECTED);
    }

    @Test
    void shouldThrowValidationExceptionWhenStartAfterEnd() {
        when(itemService.findItem(anyLong())).thenReturn(itemResponse);
        when(userService.getUser(anyLong())).thenReturn(userResponse);

        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 1L))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void shouldThrowValidationExceptionWhenStartEqualEnd() {
        when(itemService.findItem(anyLong())).thenReturn(itemResponse);
        when(userService.getUser(anyLong())).thenReturn(userResponse);

        LocalDateTime now = LocalDateTime.now();

        CreateBookingRequest createBookingRequest = CreateBookingRequest.builder()
                .itemId(1L)
                .start(now)
                .end(now)
                .build();

        assertThatThrownBy(() -> bookingService.createBooking(createBookingRequest, 1L))
                .isInstanceOf(ValidationException.class);
    }
}