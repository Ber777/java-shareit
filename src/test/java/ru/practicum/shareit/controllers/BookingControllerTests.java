package ru.practicum.shareit.controllers;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class BookingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    // Настраиваем ObjectMapper для корректной работы с LocalDateTime
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String asJsonString(final Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void shouldCreateBooking() throws Exception {
        // Данные
        Long userId = 1L;
        Long itemId = 10L;

        LocalDateTime start = LocalDateTime.of(2026, 2, 12, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 14, 10, 0, 0);

        String startStr = "2026-02-12T10:00:00";
        String endStr = "2026-02-14T10:00:00";

        BookingDto requestDto = BookingDto.builder()
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        BookingDtoResponse responseDto = BookingDtoResponse.builder()
                .id(100L)
                .item(ItemDto.builder().id(itemId).name("Item1").build())
                .booker(UserDto.builder().id(userId).name("User1").build())
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.createBooking(eq(userId), eq(requestDto)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId.toString())  // Важно: toString()
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.item.id").value(itemId))
                .andExpect(jsonPath("$.booker.id").value(userId))
                .andExpect(jsonPath("$.status").value("WAITING"))
                .andExpect(jsonPath("$.start").value(startStr))
                .andExpect(jsonPath("$.end").value(endStr));
    }

    @Test
    void shouldUpdateBookingStatus() throws Exception {
        Long userId = 1L, bookingId = 100L;

        BookingDtoResponse updatedDto = BookingDtoResponse.builder()
                .id(bookingId)
                .item(ItemDto.builder().id(10L).build())
                .booker(UserDto.builder().id(userId).build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.APPROVED)
                .build();

        when(bookingService.updateBookingStatus(userId, bookingId, true))
                .thenReturn(updatedDto);


        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldGetBooking() throws Exception {
        Long userId = 1L, bookingId = 100L;

        BookingDtoResponse responseDto = BookingDtoResponse.builder()
                .id(bookingId)
                .item(ItemDto.builder().id(10L).name("Item1").build())
                .booker(UserDto.builder().id(userId).name("User1").build())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .status(BookingStatus.WAITING)
                .build();

        when(bookingService.getBooking(userId, bookingId)).thenReturn(responseDto);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.item.name").value("Item1"))
                .andExpect(jsonPath("$.booker.name").value("User1"));
    }

    @Test
    void shouldGetUserBookings() throws Exception {
        Long userId = 1L;

        List<BookingDtoResponse> bookings = List.of(
                BookingDtoResponse.builder()
                        .id(100L)
                        .item(ItemDto.builder().id(10L).name("Item1").build())
                        .booker(UserDto.builder().id(userId).build())
                        .start(LocalDateTime.now().plusDays(1))
                        .end(LocalDateTime.now().plusDays(3))
                        .status(BookingStatus.WAITING)
                        .build(),
                BookingDtoResponse.builder()
                        .id(101L)
                        .item(ItemDto.builder().id(11L).name("Item2").build())
                        .booker(UserDto.builder().id(userId).build())
                        .start(LocalDateTime.now().plusDays(2))
                        .end(LocalDateTime.now().plusDays(4))
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        when(bookingService.getUserBookings(userId, "ALL", 0, 10))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(100L))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    void shouldGetOwnerBookings() throws Exception {
        Long userId = 2L;

        LocalDateTime start = LocalDateTime.of(2026, 2, 10, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 2, 12, 10, 0, 0);

        List<BookingDtoResponse> bookings = List.of(
                BookingDtoResponse.builder()
                        .id(200L)
                        .item(ItemDto.builder().id(20L).name("Owned Item").build())
                        .booker(UserDto.builder().id(1L).name("Booker1").build())
                        .start(start)
                        .end(end)
                        .status(BookingStatus.APPROVED)
                        .build()
        );

        when(bookingService.getOwnerBookings(userId, "ALL", 0, 10))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(200L))
                .andExpect(jsonPath("$[0].item.name").value("Owned Item"))
                .andExpect(jsonPath("$[0].booker.name").value("Booker1"))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[0].start").value("2026-02-10T10:00:00"))
                .andExpect(jsonPath("$[0].end").value("2026-02-12T10:00:00"));
    }

    // Тест на валидацию: ошибка при отсутствии X-Sharer-User-Id
    @Test
    void shouldFailWhenHeaderMissing() throws Exception {
        BookingDto requestDto = BookingDto.builder()
                .itemId(10L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // Тест на валидацию: ошибка при null itemId
    @Test
    void shouldFailWhenItemIdNull() throws Exception {
        Long userId = 1L;
        BookingDto requestDto = BookingDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    // Тест на валидацию: ошибка при прошлом start
    @Test
    void createBooking_shouldFailWhenStartInPast() throws Exception {
        Long userId = 1L;
        LocalDateTime pastStart = LocalDateTime.now().minusDays(1);
        LocalDateTime end = pastStart.plusDays(2);

        BookingDto requestDto = BookingDto.builder()
                .itemId(10L)
                .start(pastStart)
                .end(end)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest());
    }
}
