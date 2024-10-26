package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemService {
    InMemoryItemStorage inMemoryItemStorage;
    InMemoryUserStorage inMemoryUserStorage;

    public Item create(Long userId, Item item) {
        // Проверяем существует ли пользователь
        inMemoryUserStorage.getUserById(userId);

        return inMemoryItemStorage.create(userId, item);
    }

    public Item update(Long userId, Long itemId, ItemDto item) {
        // Проверяем существует ли пользователь
        inMemoryUserStorage.getUserById(userId);

        return inMemoryItemStorage.update(userId, itemId, item);
    }

    public Item getItemById(Long userId, Long itemId) {
        // Проверяем существует ли пользователь
        inMemoryUserStorage.getUserById(userId);

        return inMemoryItemStorage.getItemById(userId, itemId);
    }

    public Collection<ItemDto> getAllUserItems(Long userId) {
        // Проверяем существует ли пользователь
        inMemoryUserStorage.getUserById(userId);

        return inMemoryItemStorage.getAllUserItems(userId).stream()
                .map(ItemMapper::itemDtoMapper)
                .toList();
    }

    public Collection<ItemDto> searchItem(String text) {
        return inMemoryItemStorage.searchItem(text).stream()
                .map(ItemMapper::itemDtoMapper)
                .toList();
    }
}
