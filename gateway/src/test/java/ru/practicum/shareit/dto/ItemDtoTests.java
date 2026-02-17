package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItGateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

@SpringBootTest(classes = ShareItGateway.class)
public class ItemDtoTests {
    @Autowired
    private Validator validator;

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

    @Test
    void shouldFailWhenNameIsBlank() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("")
                .description("Описание")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Название не должно быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name(null)
                .description("Описание")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Название не должно быть пустым", violation.getMessage());
    }


    @Test
    void shouldFailWhenDescriptionIsBlank() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Название")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Описание не должно быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Название")
                .description(null)
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Описание не должно быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenAvailableIsNull() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Название")
                .description("Описание")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemDto> violation = violations.iterator().next();
        assertEquals("available", violation.getPropertyPath().toString());
        assertEquals("Вещь должна быть либо доступна для аренды либо нет", violation.getMessage());
    }

    @Test
    void shouldHaveMultipleViolationsWhenAllFieldsInvalid() {
        ItemDto itemDto = ItemDto.builder()
                .id(null)
                .name("")
                .description("")
                .available(null)
                .requestId(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);

        assertEquals(3, violations.size());  // name, description, available

        // Проверяем, что все ожидаемые поля присутствуют
        Set<String> violatedFields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(java.util.stream.Collectors.toSet());

        assertTrue(violatedFields.contains("name"));
        assertTrue(violatedFields.contains("description"));
        assertTrue(violatedFields.contains("available"));
    }
}

