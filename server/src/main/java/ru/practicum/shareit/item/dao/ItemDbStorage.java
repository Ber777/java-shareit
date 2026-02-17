/*package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemDbStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item newItem) {
        Item item = Item.builder()
                .id(getNextId())
                .name(newItem.getName())
                .description(newItem.getDescription())
                .available(newItem.getAvailable())
                .owner(newItem.getOwner())
                .request(newItem.getRequest())
                .build();
        items.put(item.getId(), item);

        return item;
    }

    @Override
    public Item updateItemById(Item newItem, Long itemId) {
        Item oldItem = items.get(itemId);
        oldItem.setName(newItem.getName() != null ? newItem.getName() : oldItem.getName());
        oldItem.setDescription(newItem.getDescription() != null ? newItem.getDescription() : oldItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable() != null ? newItem.getAvailable() : oldItem.getAvailable());
        oldItem.setRequest(newItem.getRequest() != null ? newItem.getRequest() : oldItem.getRequest());

        items.put(oldItem.getId(), oldItem);
        return oldItem;
    }

    @Override
    public Item getItemById(Long itemId) {
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        } else {
            throw new NotFoundException(String.format("Вещь с id %d не найдена", itemId));
        }
    }

    @Override
    public Collection<Item> getItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(i -> userId.equals(i.getOwner().getId()))
                .toList();
    }

    @Override
    public Collection<Item> searchItemsByText(String text) {
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
*/