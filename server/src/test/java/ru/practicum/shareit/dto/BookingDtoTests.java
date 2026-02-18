package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest(classes = ShareItServer.class)
public class BookingDtoTests {
    private BookingDto validBookingDto;

    @BeforeEach
    void setUp() {
        validBookingDto = BookingDto.builder()
                .id(1L)
                .itemId(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validBookingDto.getId());
        assertEquals(100L, validBookingDto.getItemId());
        assertTrue(validBookingDto.getStart().isAfter(LocalDateTime.now()));
        assertTrue(validBookingDto.getEnd().isAfter(validBookingDto.getStart()));
    }
}
