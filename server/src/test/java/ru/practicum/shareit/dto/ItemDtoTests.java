package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.dto.ItemDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
public class ItemDtoTests {
    private ItemDto validItemDto;

    @BeforeEach
    void setUp() {
        validItemDto = ItemDto.builder()
                .id(1L)
                .name("Ноутбук")
                .description("Мощный ноутбук для работы")
                .available(true)
                .requestId(100L)
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validItemDto.getId());
        assertEquals("Ноутбук", validItemDto.getName());
        assertEquals("Мощный ноутбук для работы", validItemDto.getDescription());
        assertTrue(validItemDto.getAvailable());
        assertEquals(100L, validItemDto.getRequestId());
    }
}
