package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    ItemDto createItem(Long userId, ItemDto itemDto);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoResponse getItem(Long userId, Long itemId);

    Collection<ItemDtoResponse> getUserItems(Long userId);

    Collection<ItemDtoResponse> getItemsByText(String text);

    void deleteItem(Long itemId);
}
