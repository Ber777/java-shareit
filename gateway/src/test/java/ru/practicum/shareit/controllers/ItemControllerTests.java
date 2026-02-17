package ru.practicum.shareit.controllers;

import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.ItemDto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean  // Добавляем мок для ItemClient
    private ItemClient itemClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Утилита для конвертации в JSON
    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void shouldReturnFailWhenNameIsEmpty() throws Exception {
        Long userId = 1L;
        ItemDto invalidDto = ItemDto.builder()
                .name("")
                .description("Описание")
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnFailWhenDescriptionIsNull() throws Exception {
        Long userId = 1L;
        ItemDto invalidDto = ItemDto.builder()
                .name("Имя")
                .description(null)
                .available(true)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnFailWhenAvailableIsNull() throws Exception {
        Long userId = 1L;
        ItemDto invalidDto = ItemDto.builder()
                .name("Имя")
                .description("Описание")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidDto)))
                .andExpect(status().isBadRequest());
    }
}
