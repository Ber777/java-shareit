package ru.practicum.shareit.controllers;

import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestDto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void shouldReturn400WhenDescriptionIsBlank() throws Exception {
        Long userId = 1L;
        ItemRequestDto invalidDto = ItemRequestDto.builder()
                .description("")  // Пустое описание
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDescriptionIsNull() throws Exception {
        Long userId = 1L;
        ItemRequestDto invalidDto = ItemRequestDto.builder().build();  // Нет description

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnEmptyArrayWhenNoUserRequests() throws Exception {
        Long userId = 999L;
        when(itemRequestClient.getUserRequests(userId))
                .thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn404WhenRequestNotFound() throws Exception {
        Long userId = 1L, requestId = 999L;
        when(itemRequestClient.getRequestById(userId, requestId))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleValidationExceptionOnCreate() throws Exception {
        Long userId = 1L;
        ItemRequestDto invalidDto = ItemRequestDto.builder()
                .description("")  // Пустое описание вызовет валидацию
                .build();

        when(itemRequestClient.createRequest(userId, invalidDto))
                .thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenDtoValidationFails() throws Exception {
        Long userId = 1L;
        // Пропускаем обязательное поле description
        ItemRequestDto incompleteDto = new ItemRequestDto();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(incompleteDto)))
                .andExpect(status().isBadRequest());
    }
}
