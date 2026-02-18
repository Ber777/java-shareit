package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

public class BookingMapper {
    public static Booking toBooking(User user, Item item, BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStartDate(bookingDto.getStart());
        booking.setEndDate(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto toBookingDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .itemId(booking.getItem().getId())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .item(toItemDto(booking.getItem()))
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .booker(toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }
}
