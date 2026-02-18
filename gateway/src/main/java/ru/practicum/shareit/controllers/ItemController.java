package ru.practicum.shareit.controllers;

import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

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
@RequestMapping("/items")
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @Validated @RequestBody ItemDto itemDto
    ) {
        log.info("POST /items - создание вещи пользователем с id={}, данные: {}", userId, itemDto);
        return itemClient.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("POST /items/{}/comment - добавление комментария пользователем с id={}, данные: {}",
                itemId, userId, commentDto);
        return itemClient.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId,
            @RequestBody ItemDto itemDto
    ) {
        log.info("PATCH /items/{} - обновление вещи пользователем с id={}, данные: {}",
                itemId, userId, itemDto);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(
            @RequestHeader(X_SHARER_USER_ID) Long userId,
            @PathVariable Long itemId
    ) {
        log.info("GET /items/{} - запрос вещи пользователем с id={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("GET /items - запрос всех вещей пользователя с id={}", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemsByText(@RequestParam("text") String text) {
        log.info("GET /items/search?text={} - поиск вещей по тексту", text);
        return itemClient.getItemsByText(text);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        log.info("DELETE /items/{} - удаление вещи", id);
        itemClient.deleteItem(id);
    }
}
