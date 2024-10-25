package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface InMemoryItemStorage {
    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, ItemDto item);

    Item getItemById(Long userId, Long itemId);

    Collection<Item> getAllUserItems(Long userId);

    Collection<Item> searchItem(String text);
}
