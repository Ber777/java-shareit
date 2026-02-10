package ru.practicum.shareit.dto;

import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDtoResponseTests {
    private BookingDtoResponse validBookingDtoResponse;

    @BeforeEach
    void setUp() {
        validBookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .item(ItemDto.builder().id(10L).name("Ноутбук").build())
                .start(LocalDateTime.of(2026, 2, 5, 10, 0))
                .end(LocalDateTime.of(2026, 2, 9, 18, 0))
                .booker(UserDto.builder().id(20L).name("Иван").build())
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validBookingDtoResponse.getId());
        assertEquals(10L, validBookingDtoResponse.getItem().getId());
        assertEquals("Ноутбук", validBookingDtoResponse.getItem().getName());
        assertEquals(LocalDateTime.of(2026, 2, 5, 10, 0), validBookingDtoResponse.getStart());
        assertEquals(LocalDateTime.of(2026, 2, 9, 18, 0), validBookingDtoResponse.getEnd());
        assertEquals(20L, validBookingDtoResponse.getBooker().getId());
        assertEquals("Иван", validBookingDtoResponse.getBooker().getName());
        assertEquals(BookingStatus.APPROVED, validBookingDtoResponse.getStatus());
    }

    @Test
    void shouldCreateWithItemIsNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .item(null)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(UserDto.builder().id(20L).build())
                .status(BookingStatus.REJECTED)
                .build();

        assertNull(dto.getItem());
    }

    @Test
    void shouldCreateWithStartIsNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .item(ItemDto.builder().id(10L).build())
                .start(null)
                .end(LocalDateTime.now().plusDays(1))
                .booker(UserDto.builder().id(20L).build())
                .status(BookingStatus.APPROVED)
                .build();

        assertNull(dto.getStart());
    }

    @Test
    void shouldCreateWithEndIsNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .item(ItemDto.builder().id(10L).build())
                .start(LocalDateTime.now())
                .end(null)
                .booker(UserDto.builder().id(20L).build())
                .status(BookingStatus.WAITING)
                .build();

        assertNull(dto.getEnd());
    }

    @Test
    void shouldCreateWithBookerIsNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .item(ItemDto.builder().id(10L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(null)
                .status(BookingStatus.REJECTED)
                .build();

        assertNull(dto.getBooker());
    }

    @Test
    void shouldCreateWithStatusIsNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .item(ItemDto.builder().id(10L).build())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .booker(UserDto.builder().id(20L).build())
                .status(null)
                .build();

        assertNull(dto.getStatus());
    }

    @Test
    void shouldAllowAllFieldsToBeNull() {
        BookingDtoResponse dto = BookingDtoResponse.builder()
                .id(null)
                .item(null)
                .start(null)
                .end(null)
                .booker(null)
                .status(null)
                .build();

        assertNull(dto.getId());
        assertNull(dto.getItem());
        assertNull(dto.getStart());
        assertNull(dto.getEnd());
        assertNull(dto.getBooker());
        assertNull(dto.getStatus());
    }
}
