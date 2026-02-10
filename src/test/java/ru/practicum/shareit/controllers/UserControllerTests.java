package ru.practicum.shareit.controllers;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldGetAllUsersAndShouldReturnListOfUsers() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(1L, "Иван", "john@example.com"),
                new UserDto(2L, "Алексей", "alex@example.com")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Иван"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Алексей"));
    }

    @Test
    void shouldGetUserById() throws Exception {
        Long userId = 1L;
        UserDto user = new UserDto(userId, "John", "john@example.com");

        when(userService.getUserById(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserDto inputUser = new UserDto(null, "Артур", "artur@example.com");
        UserDto outputUser = new UserDto(3L, "Артур", "artur@example.com");

        when(userService.createUser(inputUser)).thenReturn(outputUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(inputUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Артур"))
                .andExpect(jsonPath("$.email").value("artur@example.com"));
    }

    @Test
    void shouldUpdateUserById() throws Exception {
        Long userId = 1L;
        UserDto updatedUser = new UserDto(userId, "Иван Обновленный", "john-new@example.com");

        when(userService.updateUserById(updatedUser, userId)).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Иван Обновленный"))
                .andExpect(jsonPath("$.email").value("john-new@example.com"));
    }

    @Test
    void shouldFailWithValidationErrorWhenEmailIsInvalid() throws Exception {
        Long userId = 1L;
        UserDto invalidUser = new UserDto(userId, "Иван", "invalid-email");

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
