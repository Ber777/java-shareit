package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.booking.dto.BookingDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class)
public class BookingDtoTests {
    @Autowired
    private Validator validator;

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

    @Test
    void shouldFailWhenItemIdIsNull() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertEquals("itemId", violation.getPropertyPath().toString());
        assertEquals("не должно равняться null", violation.getMessage());
    }

    @Test
    void shouldFailWhenStartIsNull() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(100L)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertEquals("start", violation.getPropertyPath().toString());
        assertEquals("не должно равняться null", violation.getMessage());
    }

    @Test
    void shouldFailWhenStartIsInPast() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertEquals("start", violation.getPropertyPath().toString());
        assertEquals("должно содержать сегодняшнее число или дату, которая еще не наступила", violation.getMessage());
    }

    @Test
    void shouldFailWhenEndIsNull() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(null)
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertEquals("end", violation.getPropertyPath().toString());
        assertEquals("не должно равняться null", violation.getMessage());
    }

    @Test
    void shouldFailWhenEndIsInPast() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(100L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<BookingDto> violation = violations.iterator().next();
        assertEquals("end", violation.getPropertyPath().toString());
        assertEquals("должно содержать дату, которая еще не наступила", violation.getMessage());
    }
}
