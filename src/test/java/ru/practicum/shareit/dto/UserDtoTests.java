package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.exception.Create;
import ru.practicum.shareit.exception.Update;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItApp.class)
public class UserDtoTests {
    @Autowired
    private Validator validator;

    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
        assertNotNull(validator, "Validator не должен быть null");

        validUserDto = UserDto.builder()
                .id(1L)
                .name("Иван Березин")
                .email("ivan@example.com")
                .build();
    }

    @Test
    void shouldCreateWithValidData() {
        assertEquals(1L, validUserDto.getId());
        assertEquals("Иван Березин", validUserDto.getName());
        assertEquals("ivan@example.com", validUserDto.getEmail());
    }

    @Test
    void shouldFailWhenNameIsBlank() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("")
                .email("ivan@example.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Имя пользователя не должно быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenNameIsNull() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(null)
                .email("ivan@example.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("name", violation.getPropertyPath().toString());
        assertEquals("Имя пользователя не должно быть пустым", violation.getMessage());
    }

    @Test
    void shouldFailWhenEmailInvalidFormat() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Иван Березин")
                .email("ivanexample.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(
                userDto,
                Create.class,
                Update.class
        );

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());

        ConstraintViolation<UserDto> violation = violations.iterator().next();
        assertEquals("email", violation.getPropertyPath().toString());
        assertEquals("Email должен иметь формат адреса электронной почты - символ @", violation.getMessage());
    }

    @Test
    void shouldCheckEmailValidationOnlyInCreateUpdateGroups() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Иван Березин")
                .email("invalid-email")
                .build();

        // Валидация без групп — email не проверяется
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertTrue(violations.isEmpty());

        // Валидация с группами — email проверяется
        violations = validator.validate(userDto, Create.class, Update.class);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }
}
