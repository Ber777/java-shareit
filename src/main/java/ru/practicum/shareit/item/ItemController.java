package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                              @Validated @RequestBody ItemDto itemDto) {
        log.info("POST /items - создание вещи пользователем с id={}, данные: {}", userId, itemDto);
        return itemService.createItem(userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentDto commentDto) {
        log.info("POST /items/{}/comment - добавление комментария пользователем с id={}, данные: {}",
                itemId, userId, commentDto);
        return itemService.addComment(userId, itemId, commentDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(X_SHARER_USER_ID) Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} - обновление вещи пользователем с id={}, данные: {}",
                itemId, userId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoResponse getItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable Long itemId) {
        log.info("GET /items/{} - запрос вещи пользователем с id={}", itemId, userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDtoResponse> getUserItems(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        log.info("GET /items - запрос всех вещей пользователя с id={}", userId);
        return new ArrayList<>(itemService.getUserItems(userId));
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getItemsByText(@RequestParam("text") String text) {
        log.info("GET /items/search?text={} - поиск вещей по тексту", text);
        return new ArrayList<>(itemService.getItemsByText(text));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        log.info("DELETE /items/{} - удаление вещи", id);
        itemService.deleteItem(id);
    }
}
