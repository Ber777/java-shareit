package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;
    private final UserService userService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody @Valid ItemDto itemDto) {
        UserDto userDto = userService.getUserById(userId);
        return itemService.createItem(itemDto, userDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItemById(@RequestHeader(X_SHARER_USER_ID) Long userId, @RequestBody ItemDto itemDto,
                                  @PathVariable("itemId") Long itemId) {
        userService.getUserById(userId);
        return itemService.updateItemById(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader(X_SHARER_USER_ID) Long userId, @PathVariable("itemId") Long itemId) {
        userService.getUserById(userId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUserId(@RequestHeader(X_SHARER_USER_ID) Long userId) {
        userService.getUserById(userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItemsByText(@RequestHeader(X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(name = "text") String text) {
        userService.getUserById(userId);
        return itemService.searchItemsByText(text);
    }
}
