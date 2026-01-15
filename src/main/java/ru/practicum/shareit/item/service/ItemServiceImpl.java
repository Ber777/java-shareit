package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final String ITEM_NOT_FOUND_ERR = "Вещь с id %d не найдена";
    private final ItemStorage itemStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, UserDto userDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userDto));

        return ItemMapper.toItemDto(itemStorage.createItem(item));
    }

    @Override
    public ItemDto updateItemById(ItemDto itemDto, Long userId, Long itemId) {
        if (isUserOwnerOfItem(itemId, userId)) {
            return ItemMapper.toItemDto(itemStorage.updateItemById(ItemMapper.toItem(itemDto), itemId));
        } else {
            throw new ValidationException(String.format("Вещь с id %d не принадлежит пользователю с id %d",
                    itemId, userId));
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemStorage.getItemById(itemId);
        if (item != null) {
            return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
        } else {
            throw new ValidationException(String.format(ITEM_NOT_FOUND_ERR, itemId));
        }
    }

    @Override
    public Collection<ItemDto> getItemsByUserId(Long userId) {
        return itemStorage.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    @Override
    public Collection<ItemDto> searchItemsByText(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .toList();
    }

    private Boolean isUserOwnerOfItem(Long itemId, Long userId) {
        Item item = itemStorage.getItemById(itemId);
        if (item != null) {
            return userId.equals(item.getOwner().getId());
        } else {
            throw new ValidationException(String.format(ITEM_NOT_FOUND_ERR, itemId));
        }
    }
}
