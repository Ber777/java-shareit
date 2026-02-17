package ru.practicum.shareit.controllers;

import ru.practicum.shareit.Update;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.UserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /users - получение всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable("userId") Long userId) {
        log.info("GET /users/{} - получение пользователя по id", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@Validated @RequestBody UserDto userDto) {
        log.info("POST /users - создание нового пользователя: {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUserById(
            @Validated(Update.class)
            @RequestBody UserDto userDto,
            @PathVariable("userId") Long userId
    ) {
        log.info("PATCH /users/{} - обновление пользователя: {}", userId, userDto);
        return userClient.updateUserById(userDto, userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {
        log.info("DELETE /users/{} - удаление пользователя", userId);
        userClient.deleteUser(userId);
    }
}
