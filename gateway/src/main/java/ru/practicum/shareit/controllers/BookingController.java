package ru.practicum.shareit.controllers;

import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.client.BookingClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    public static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createBooking(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @Valid @RequestBody BookingDto bookingDto
    ) {
        log.info("POST /bookings - создание бронирования от пользователя с id={}, данные: {}", userId, bookingDto);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @PathVariable @Positive Long bookingId,
            @RequestParam Boolean approved
    ) {
        log.info("PATCH /bookings/{} - обновление статуса бронирования от пользователя с id={}, новый статус: {}",
                bookingId, userId, approved ? "APPROVED" : "REJECTED");
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @PathVariable @Positive Long bookingId
    ) {
        log.info("GET /bookings/{} - получение бронирования по id от пользователя с id={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("GET /bookings?state={}&from={}&size={} - получение списка бронирований пользователя с id={}",
                state, from, size, userId);
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(
            @RequestHeader(X_SHARER_USER_ID) @Positive Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        log.info("GET /bookings/owner?state={}&from={}&size={} - получение списка бронирований владельца с id={}",
                state, from, size, userId);
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
