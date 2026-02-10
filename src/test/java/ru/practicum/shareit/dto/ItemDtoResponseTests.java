package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = ShareItApp.class)
public class ItemDtoResponseTests {
    private ItemDtoResponse validItemDtoResponse;

    @BeforeEach
    void setUp() {
        validItemDtoResponse = ItemDtoResponse.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Мощный ноутбук для работы")
                .available(true)
                .requestId(100L)
                .bookings(List.of(
                        BookingDto.builder().id(10L).build()
                ))
                .comments(List.of(
                        CommentDto.builder().id(5L).text("Отличный товар!").build()
                ))
                .lastBooking(LocalDateTime.of(2026, 1, 15, 10, 0))
                .nextBooking(LocalDateTime.of(2026, 3, 1, 14, 0))
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validItemDtoResponse.getId());
        assertEquals("Ноутбук", validItemDtoResponse.getName());
        assertEquals("Мощный ноутбук для работы", validItemDtoResponse.getDescription());
        assertTrue(validItemDtoResponse.getAvailable());
        assertEquals(100L, validItemDtoResponse.getRequestId());
        assertEquals(1, validItemDtoResponse.getBookings().size());
        assertEquals(1, validItemDtoResponse.getComments().size());
        assertEquals(LocalDateTime.of(2026, 1, 15, 10, 0), validItemDtoResponse.getLastBooking());
        assertEquals(LocalDateTime.of(2026, 3, 1, 14, 0), validItemDtoResponse.getNextBooking());
    }

    @Test
    void shouldCreateWithNameIsNull() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .name(null)
                .description("Описание")
                .available(true)
                .build();

        assertNull(itemDtoResponse.getName());
    }

    @Test
    void shouldCreateWithDescriptionIsNull() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .name("Название")
                .description(null)
                .available(true)
                .build();

        assertNull(itemDtoResponse.getDescription());
    }

    @Test
    void shouldCreateWithAvailableIsNull() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .name("Название")
                .description("Описание")
                .available(null)
                .build();

        assertNull(itemDtoResponse.getAvailable());
    }

    @Test
    void shouldCreateWithBookingsIsEmptyOrNull() {
        ItemDtoResponse withEmptyBookings = ItemDtoResponse.builder()
                .bookings(List.of())
                .build();
        assertTrue(withEmptyBookings.getBookings().isEmpty());

        ItemDtoResponse withNullBookings = ItemDtoResponse.builder()
                .bookings(null)
                .build();
        assertNull(withNullBookings.getBookings());
    }

    @Test
    void shouldCreateWithCommentsIsEmptyOrNull() {
        ItemDtoResponse withEmptyComments = ItemDtoResponse.builder()
                .comments(List.of())
                .build();
        assertTrue(withEmptyComments.getComments().isEmpty());

        ItemDtoResponse withNullComments = ItemDtoResponse.builder()
                .comments(null)
                .build();
        assertNull(withNullComments.getComments());
    }

    @Test
    void shouldCreateWithLastBookingIsNull() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .lastBooking(null)
                .build();

        assertNull(itemDtoResponse.getLastBooking());
    }

    @Test
    void shouldCreateWithCommentsAlwaysIncludedInJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        // Случай: comments = null
        ItemDtoResponse withNullComments = ItemDtoResponse.builder().comments(null).build();
        String jsonWithNull = objectMapper.writeValueAsString(withNullComments);
        assertTrue(jsonWithNull.contains("\"comments\":null"));

        // Случай: comments = пустой список
        ItemDtoResponse withEmptyComments = ItemDtoResponse.builder().comments(List.of()).build();
        String jsonWithEmpty = objectMapper.writeValueAsString(withEmptyComments);
        assertTrue(jsonWithEmpty.contains("\"comments\":[]"));
    }
}
