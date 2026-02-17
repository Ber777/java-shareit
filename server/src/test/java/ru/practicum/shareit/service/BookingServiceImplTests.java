package ru.practicum.shareit.service;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookingServiceImplTests {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private EntityManager em;

    private Long bookerId;
    private Long ownerId;
    private Long itemId;
    private Long bookingId;

    @BeforeEach
    void setUp() {
        // Создаём пользователя‑арендатора (booker)
        UserDto bookerDto = makeUserDto("booker@example.com", "Иван", "Иванов");
        bookerDto = userService.createUser(bookerDto);
        bookerId = bookerDto.getId();

        // Создаём владельца вещи (owner)
        UserDto ownerDto = makeUserDto("owner@example.com", "Алексей", "Петров");
        ownerDto = userService.createUser(ownerDto);
        ownerId = ownerDto.getId();

        // Создаём доступную вещь (принадлежит owner)
        ItemDto itemDto = makeItemDto("Велосипед", "Горный велосипед", true);
        itemDto = itemService.createItem(ownerId, itemDto);
        itemId = itemDto.getId();

        // Создаём бронирование (от booker на item)
        BookingDto bookingDto = makeBookingDto(itemId, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDtoResponse bookingResponse = bookingService.createBooking(bookerId, bookingDto);
        bookingId = bookingResponse.getId();
    }

    // Вспомогательные методы
    private UserDto makeUserDto(String email, String firstName, String lastName) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(firstName + " " + lastName);
        return dto;
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        ItemDto dto = new ItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        return dto;
    }

    private BookingDto makeBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingDto dto = new BookingDto();
        dto.setItemId(itemId);
        dto.setStart(start);
        dto.setEnd(end);
        return dto;
    }

    @Test
    void shouldCreateBooking() {
        LocalDateTime start = LocalDateTime.now().plusDays(3);
        LocalDateTime end = LocalDateTime.now().plusDays(4);
        BookingDto bookingDto = makeBookingDto(itemId, start, end);

        BookingDtoResponse created = bookingService.createBooking(bookerId, bookingDto);

        // Проверка DTO
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getItem().getId()).isEqualTo(itemId);
        assertThat(created.getBooker().getId()).isEqualTo(bookerId);
        assertThat(created.getStart()).isEqualTo(start);
        assertThat(created.getEnd()).isEqualTo(end);
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING);

        // Проверка в БД
        Booking bookingInDb = em.find(Booking.class, created.getId());
        assertThat(bookingInDb).isNotNull();
        assertThat(bookingInDb.getItem().getId()).isEqualTo(itemId);
        assertThat(bookingInDb.getBooker().getId()).isEqualTo(bookerId);
        assertThat(bookingInDb.getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnCreateBooking() {
        BookingDto bookingDto = makeBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(999L, bookingDto);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenItemDoesNotExistOnCreateBooking() {
        BookingDto bookingDto = makeBookingDto(999L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookerId, bookingDto);
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenItemNotAvailableOnCreateBooking() {
        // Делаем вещь недоступной
        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);
        itemService.updateItem(ownerId, itemId, updateDto);

        BookingDto bookingDto = makeBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookerId, bookingDto);
        });
    }

    @Test
    void shouldUpdateBookingStatusToApproved() {
        BookingDtoResponse updated = bookingService.updateBookingStatus(ownerId, bookingId, true);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(bookingId);
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.APPROVED);

        // Проверка в БД
        Booking bookingInDb = em.find(Booking.class, bookingId);
        assertThat(bookingInDb.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }

    @Test
    void shouldUpdateBookingStatusToRejected() {
        BookingDtoResponse updated = bookingService.updateBookingStatus(ownerId, bookingId, false);

        assertThat(updated.getStatus()).isEqualTo(BookingStatus.REJECTED);

        Booking bookingInDb = em.find(Booking.class, bookingId);
        assertThat(bookingInDb.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingDoesNotExistOnUpdateStatus() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.updateBookingStatus(ownerId, 999L, true);
        });
    }

    @Test
    void shouldThrowValidationExceptionWhenNonOwnerUpdatesStatus() {
        // Пытаемся обновить статус не от имени владельца вещи
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.updateBookingStatus(bookerId, bookingId, true);
        });
    }

    @Test
    void shouldGetBookingAsBooker() {
        BookingDtoResponse response = bookingService.getBooking(bookerId, bookingId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(bookingId);
        assertThat(response.getBooker().getId()).isEqualTo(bookerId);
    }

    @Test
    void shouldThrowValidationExceptionWhenUserIsNotBookerOrOwner() {
        // Создаём третьего пользователя
        UserDto thirdUserDto = makeUserDto("third@example.com", "Мария", "Сидорова");
        thirdUserDto = userService.createUser(thirdUserDto);
        Long thirdUserId = thirdUserDto.getId();

        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getBooking(thirdUserId, bookingId);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenBookingDoesNotExist() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(bookerId, 999L);
        });
    }

    @Test
    void shouldGetUserBookingsPast() {
        // Имитируем прошлое бронирование (изменяем даты)
        Booking booking = em.find(Booking.class, bookingId);
        booking.setStartDate(LocalDateTime.now().minusDays(3));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        em.flush();

        Collection<BookingDtoResponse> bookings = bookingService.getUserBookings(bookerId, "PAST", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldGetUserBookingsFuture() {
        Collection<BookingDtoResponse> bookings = bookingService.getUserBookings(bookerId, "FUTURE", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void shouldGetUserBookingsWaiting() {
        Collection<BookingDtoResponse> bookings = bookingService.getUserBookings(bookerId, "WAITING", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldGetUserBookingsRejected() {
        // Сначала отклоняем бронирование
        bookingService.updateBookingStatus(ownerId, bookingId, false);

        Collection<BookingDtoResponse> bookings = bookingService.getUserBookings(bookerId, "REJECTED", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldGetUserBookingsAll() {
        Collection<BookingDtoResponse> bookings = bookingService.getUserBookings(bookerId, "ALL", 0, 10);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldThrowValidationExceptionOnUnknownState() {
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getUserBookings(bookerId, "UNKNOWN_STATE", 0, 10);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnGetUserBookings() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getUserBookings(999L, "ALL", 0, 10);
        });
    }

    @Test
    void shouldGetOwnerBookingsPast() {
        // Имитируем прошлое бронирование
        Booking booking = em.find(Booking.class, bookingId);
        booking.setStartDate(LocalDateTime.now().minusDays(3));
        booking.setEndDate(LocalDateTime.now().minusDays(1));
        em.flush();

        Collection<BookingDtoResponse> bookings = bookingService.getOwnerBookings(ownerId, "PAST", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldGetOwnerBookingsFuture() {
        Collection<BookingDtoResponse> bookings = bookingService.getOwnerBookings(ownerId, "FUTURE", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void shouldGetOwnerBookingsWaiting() {
        Collection<BookingDtoResponse> bookings = bookingService.getOwnerBookings(ownerId, "WAITING", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getStatus()).isEqualTo(BookingStatus.WAITING);
    }

    @Test
    void shouldGetOwnerBookingsRejected() {
        bookingService.updateBookingStatus(ownerId, bookingId, false);

        Collection<BookingDtoResponse> bookings = bookingService.getOwnerBookings(ownerId, "REJECTED", 0, 10);

        assertThat(bookings).isNotEmpty();
        assertThat(bookings.iterator().next().getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldGetOwnerBookingsAll() {
        Collection<BookingDtoResponse> bookings = bookingService.getOwnerBookings(ownerId, "ALL", 0, 10);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.iterator().next().getId()).isEqualTo(bookingId);
    }

    @Test
    void shouldThrowValidationExceptionOnUnknownStateForOwner() {
        Assertions.assertThrows(ValidationException.class, () -> {
            bookingService.getOwnerBookings(ownerId, "UNKNOWN_STATE", 0, 10);
        });
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExistOnGetOwnerBookings() {
        Assertions.assertThrows(NotFoundException.class, () -> {
            bookingService.getOwnerBookings(999L, "ALL", 0, 10);
        });
    }
}
