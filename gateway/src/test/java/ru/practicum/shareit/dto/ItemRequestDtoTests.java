package ru.practicum.shareit.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItGateway;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItGateway.class)
public class ItemRequestDtoTests {

    @Autowired
    private Validator validator;

    private ItemRequestDto validItemRequestDto;

    @BeforeEach
    void setUp() {
        validItemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .userId(2L)
                .description("Подробное описание запроса")
                .created(LocalDateTime.now())
                .items(List.of(ItemDto.builder().id(10L).build()))
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validItemRequestDto.getId());
        assertEquals(2L, validItemRequestDto.getUserId());
        assertEquals("Подробное описание запроса", validItemRequestDto.getDescription());
        assertNotNull(validItemRequestDto.getCreated());
        assertEquals(1, validItemRequestDto.getItems().size());
    }

    @Test
    void shouldFailWhenDescriptionIsBlank() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .userId(2L)
                .description("")
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemRequestDto> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Описание не может быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenDescriptionIsNull() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .userId(2L)
                .description(null)
                .created(LocalDateTime.now())
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<ItemRequestDto> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Описание не может быть пустым", violation.getMessage());
    }

    @Test
    void shouldPassWhenAllFieldsAreNullExceptDescription() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .userId(null)
                .description("Непустое описание")
                .created(null)
                .items(null)
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveSingleViolationWhenDescriptionInvalid() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(null)
                .userId(null)
                .description("")
                .created(null)
                .items(null)
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(itemRequestDto);

        assertEquals(1, violations.size());

        ConstraintViolation<ItemRequestDto> violation = violations.iterator().next();
        assertEquals("description", violation.getPropertyPath().toString());
        assertEquals("Описание не может быть пустым", violation.getMessage());
    }

    @Test
    void shouldHandleEmptyItemsListGracefully() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .userId(2L)
                .description("Описание")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

        assertNotNull(dto.getItems());
        assertTrue(dto.getItems().isEmpty());
    }

    @Test
    void shouldAllowNullItemsList() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .userId(2L)
                .description("Описание")
                .created(LocalDateTime.now())
                .items(null)
                .build();

        assertNull(dto.getItems());
    }
}
