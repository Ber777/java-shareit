package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDtoResponse createBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestBody BookingDto bookingDto
    ) {
        log.info("POST /bookings - создание бронирования от пользователя с id={}, данные: {}", userId, bookingDto);
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBookingStatus(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId,
            @RequestParam boolean approved
    ) {
        log.info("PATCH /bookings/{} - обновление статуса бронирования от пользователя с id={}, новый статус: {}",
                bookingId, userId, approved ? "APPROVED" : "REJECTED");
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBooking(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long bookingId
    ) {
        log.info("GET /bookings/{} - получение бронирования по id от пользователя с id={}", bookingId, userId);
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDtoResponse> getUserBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /bookings?state={}&from={}&size={} - получение списка бронирований пользователя с id={}",
                state, from, size, userId);
        return bookingService.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoResponse> getOwnerBookings(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /bookings/owner?state={}&from={}&size={} - получение списка бронирований владельца с id={}",
                state, from, size, userId);
        return bookingService.getOwnerBookings(userId, state, from, size);
    }
}
