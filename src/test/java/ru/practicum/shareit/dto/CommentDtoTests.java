package ru.practicum.shareit.dto;

import ru.practicum.shareit.item.dto.CommentDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentDtoTests {
    private CommentDto validCommentDto;

    @BeforeEach
    void setUp() {
        validCommentDto = CommentDto.builder()
                .id(1L)
                .text("Отличный товар!")
                .authorName("Иван")
                .created(LocalDateTime.of(2026, 2, 10, 12, 0))
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validCommentDto.getId());
        assertEquals("Отличный товар!", validCommentDto.getText());
        assertEquals("Иван", validCommentDto.getAuthorName());
        assertEquals(LocalDateTime.of(2026, 2, 10, 12, 0), validCommentDto.getCreated());
    }

    @Test
    void shouldCreateWithTextIsNull() {
        CommentDto commentDto = CommentDto.builder()
                .text(null)
                .authorName("Автор")
                .created(LocalDateTime.now())
                .build();

        assertNull(commentDto.getText());
    }

    @Test
    void shouldCreateWithAuthorNameIsNull() {
        CommentDto commentDto = CommentDto.builder()
                .text("Текст")
                .authorName(null)
                .created(LocalDateTime.now())
                .build();

        assertNull(commentDto.getAuthorName());
    }

    @Test
    void shouldCreateWithCreatedIsNull() {
        CommentDto commentDto = CommentDto.builder()
                .text("Текст")
                .authorName("Автор")
                .created(null)
                .build();

        assertNull(commentDto.getCreated());
    }

    @Test
    void shouldAllowAllFieldsToBeNull() {
        CommentDto commentDto = CommentDto.builder()
                .id(null)
                .text(null)
                .authorName(null)
                .created(null)
                .build();

        assertNull(commentDto.getId());
        assertNull(commentDto.getText());
        assertNull(commentDto.getAuthorName());
        assertNull(commentDto.getCreated());
    }
}
