package ru.practicum.shareit.controllers;

import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
public class BookingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Добавляем мок для BookingClient
    private BookingClient bookingClient;

    // Настраиваем ObjectMapper для корректной работы с LocalDateTime
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private String asJsonString(final Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

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
