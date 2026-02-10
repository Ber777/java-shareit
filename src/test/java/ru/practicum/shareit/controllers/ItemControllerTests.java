package ru.practicum.shareit.controllers;

import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.Mockito.*;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@SpringBootTest(classes = ShareItApp.class)
@AutoConfigureMockMvc
public class ItemControllerTests {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private ItemService itemService;

        private final ObjectMapper objectMapper = new ObjectMapper();

        // Утилита для конвертации в JSON
        private String asJsonString(Object obj) throws Exception {
            return objectMapper.writeValueAsString(obj);
        }

        @Test
        void shouldCreateItem() throws Exception {
            Long userId = 1L;
            ItemDto inputDto = ItemDto.builder()
                    .name("Ноутбук")
                    .description("Мощный")
                    .available(true)
                    .build();
            ItemDto outputDto = ItemDto.builder()
                    .id(1L)
                    .name("Ноутбук")
                    .description("Мощный")
                    .available(true)
                    .build();

            when(itemService.createItem(userId, inputDto)).thenReturn(outputDto);

            mockMvc.perform(post("/items")
                            .header("X-Sharer-User-Id", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(inputDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Ноутбук"));
        }

    @Test
    void shouldAddComment() throws Exception {
        Long userId = 1L;
        Long itemId = 10L;

        CommentDto commentDto = CommentDto.builder()
                .text("Превосходно!")
                .authorName("Пользователь1")
                .created(null)
                .build();

        when(itemService.addComment(userId, itemId, commentDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Превосходно!"))
                .andExpect(jsonPath("$.authorName").value("Пользователь1"));
    }

        @Test
        void shouldUpdateItem() throws Exception {
            Long userId = 1L, itemId = 10L;
            ItemDto updateDto = ItemDto.builder()
                    .name("Обновленный ноутбук")
                    .description("Новое описание")
                    .available(false)
                    .build();
            ItemDto updatedDto = ItemDto.builder()
                    .id(itemId)
                    .name("Обновленный ноутбук")
                    .description("Новое описание")
                    .available(false)
                    .build();

            when(itemService.updateItem(userId, itemId, updateDto)).thenReturn(updatedDto);

            mockMvc.perform(patch("/items/{itemId}", itemId)
                            .header("X-Sharer-User-Id", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Обновленный ноутбук"))
                    .andExpect(jsonPath("$.available").value(false));
        }

    @Test
    void shouldReturnItem() throws Exception {
        Long userId = 1L, itemId = 10L;
        ItemDtoResponse responseDto = ItemDtoResponse.builder()
                .id(itemId)
                .name("Ноутбук")
                .description("Мощный ноутбук")
                .build();

        when(itemService.getItem(userId, itemId)).thenReturn(responseDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Ноутбук"))
                .andExpect(jsonPath("$.description").value("Мощный ноутбук"));
    }

    @Test
    void shouldReturnUserItems() throws Exception {
        Long userId = 1L;
        List<ItemDtoResponse> items = List.of(
                ItemDtoResponse.builder()
                        .id(1L)
                        .name("Ноутбук")
                        .description("Описани1")
                        .build(),
                ItemDtoResponse.builder()
                        .id(2L)
                        .name("Телефон")
                        .description("Описание2")
                        .build()
        );

        when(itemService.getUserItems(userId)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Ноутбук"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Телефон"));
    }

    @Test
    void shouldReturnItemsByText() throws Exception {
        String text = "ноутбук";
        List<ItemDtoResponse> items = List.of(
                ItemDtoResponse.builder()
                        .id(1L)
                        .name("Игровой ноутбук")
                        .description("Быстрый и мощный")
                        .build()
        );

        when(itemService.getItemsByText(text)).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").isString())
                .andExpect(jsonPath("$[0].description").value("Быстрый и мощный"));
    }

    @Test
    void shouldReturnNoContent() throws Exception {
        Long itemId = 10L;

        doNothing().when(itemService).deleteItem(itemId);

        mockMvc.perform(delete("/items/{id}", itemId))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).deleteItem(itemId);
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
