package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.Collection;

public interface BookingService {
    BookingDtoResponse createBooking(Long userId, BookingDto bookingDto);

    BookingDtoResponse updateBookingStatus(Long userId, Long bookingId, boolean isApproved);

    BookingDtoResponse getBooking(Long userId, Long bookingId);

    Collection<BookingDtoResponse> getUserBookings(Long userId, String state, int from, int size);

    Collection<BookingDtoResponse> getOwnerBookings(Long userId, String state, int from, int size);
}
