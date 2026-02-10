package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, UserDto userDto);

    ItemDto updateItemById(ItemDto itemDto, Long userId, Long itemId);

    ItemDto getItemById(Long itemId);

    Collection<ItemDto> getItemsByUserId(Long userId);

    Collection<ItemDto> searchItemsByText(String text);
}
