package ru.practicum.shareit.dto;

import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
public class UserDtoTests {
    private UserDto validUserDto;

    @BeforeEach
    void setUp() {
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
}
