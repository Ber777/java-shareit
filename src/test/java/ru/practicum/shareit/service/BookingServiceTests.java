package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTests {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private final LocalDateTime now = LocalDateTime.now();

    // Объекты для тестов
    private User booker;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        booker.setName("Алексей");
        booker.setEmail("alex@example.com");

        User owner = new User();
        owner.setId(2L);
        owner.setName("Иван");
        owner.setEmail("ivan@example.com");

        item = new Item();
        item.setId(10L);
        item.setName("Ноутбук");
        item.setDescription("Игровой");
        item.setAvailable(true);
        item.setOwner(owner);

        booking = new Booking();
        booking.setId(100L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStartDate(now.plusDays(1));
        booking.setEndDate(now.plusDays(2));
        booking.setStatus(BookingStatus.WAITING);

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();
    }

    @Test
    void shouldCreateBooking() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(10L))).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse result = bookingService.createBooking(1L, bookingDto);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());

        verify(userRepository).findById(1L);
        verify(itemRepository).findById(10L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(1L, bookingDto)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 1 не найден"));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemNotFound() {
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(10L))).thenReturn(Optional.empty());


        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.createBooking(1L, bookingDto)
        );

        assertTrue(exception.getMessage().contains("Вещь с id 10 не найдена"));
    }

    @Test
    void shouldThrowValidationExceptionWhenItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(booker));
        when(itemRepository.findById(eq(10L))).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.createBooking(1L, bookingDto)
        );

        assertTrue(exception.getMessage().contains("Товар недоступен для бронирования"));
    }

    @Test
    void shouldUpdateBookingStatusToApprove() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.updateBookingStatus(2L, 100L, true);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        verify(bookingRepository).findById(100L);
    }

    @Test
    void shouldUpdateBookingStatusToReject() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.updateBookingStatus(2L, 100L, false);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFound() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.updateBookingStatus(2L, 100L, true)
        );

        assertTrue(exception.getMessage().contains("Бронирование с id 100 не найдено"));
    }

    @Test
    void shouldThrowValidationExceptionWhenNotOwner() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.updateBookingStatus(1L, 100L, true)
        );

        assertTrue(exception.getMessage().contains("Только владелец может обновить статус бронирования товара"));
    }

    @Test
    void shouldGetBookingWhenBooker() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.getBooking(1L, 100L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void shouldGetBookingWhenOwner() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.of(booking));

        BookingDtoResponse result = bookingService.getBooking(2L, 100L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingNotFoundByGetBooking() {
        when(bookingRepository.findById(eq(100L))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getBooking(1L, 100L)
        );

        assertTrue(exception.getMessage().contains("Бронирование с id 100 не найдено"));
    }

    @Test
    void shouldReturnFutureBookingsWhenStateFuture() {
        List<Booking> bookings = Collections.singletonList(booking);
        Page<Booking> page = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStartDateIsAfter(
                eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        Collection<BookingDtoResponse> result = bookingService.getUserBookings(1L, "FUTURE", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(BookingStatus.WAITING, new ArrayList<>(result).getFirst().getStatus());

        verify(userRepository).existsById(1L);
        verify(bookingRepository).findByBookerIdAndStartDateIsAfter(any(), any(), any());
    }

    @Test
    void shouldReturnPastBookingsWhenStatePast() {
        List<Booking> bookings = Collections.singletonList(booking);
        Page<Booking> page = new PageImpl<>(bookings, PageRequest.of(0, 10), bookings.size());

        when(userRepository.existsById(eq(1L))).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndDateIsBefore(
                eq(1L), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(page);

        Collection<BookingDtoResponse> result = bookingService.getUserBookings(1L, "PAST", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepository).existsById(1L);
        verify(bookingRepository).findByBookerIdAndEndDateIsBefore(any(), any(), any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFoundByGetUserBooking() {
        when(userRepository.existsById(eq(1L))).thenReturn(false);


        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getUserBookings(1L, "ALL", 0, 10)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 1 не найден"));
        verify(userRepository).existsById(1L);
    }

    @Test
    void shouldThrowValidationExceptionWhenInvalidState() {
        when(userRepository.existsById(eq(1L))).thenReturn(true);


        ValidationException exception = assertThrows(ValidationException.class, () ->
                bookingService.getUserBookings(1L, "INVALID", 0, 10)
        );

        assertTrue(exception.getMessage().contains("Неизвестное состояние INVALID"));
        verify(userRepository).existsById(1L);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenOwnerNotFoundByGetOwnerBooking() {
        when(userRepository.existsById(eq(2L))).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                bookingService.getOwnerBookings(2L, "ALL", 0, 10)
        );

        assertTrue(exception.getMessage().contains("Пользователь с id 2 не найден"));
        verify(userRepository).existsById(2L);
    }
}
