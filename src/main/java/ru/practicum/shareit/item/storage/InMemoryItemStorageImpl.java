package ru.practicum.shareit.item.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class InMemoryItemStorageImpl implements InMemoryItemStorage {
    // используем счетчик для установки id вещи
    Long counter = 0L;
    final Map<Long, List<Item>> items = new HashMap<>();

    // Добавляем новую вещь пользователя
    @Override
    public Item create(Long userId, Item item) {
        log.debug("Добавляем новую вещь пользователя.");
        item.setId(counter += 1);

        // Во избежание ошибки UnsupportedOperationException создаем новый список,
        // добавляем в него новые значения и заменяем им старый
        if (items.containsKey(userId)) {
            List<Item> userItems = new ArrayList<>(items.get(userId));
            userItems.add(item);
            items.replace(userId, userItems);
        } else {
            items.put(userId, List.of(item));
        }

        log.debug("Вещь пользователя успешно добавлена.");
        return item;
    }

    // Обновляем данные вещи пользователя
    @Override
    public Item update(Long userId, Long itemId, ItemDto item) {
        log.debug("Обновляем данные вещи пользователя.");
        // Проверяем есть ли такая вещь
        Item itemToChange = getItemById(userId, itemId);

        if (item.getName() != null && !item.getName().isBlank()) {
            itemToChange.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToChange.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null && item.getAvailable() != itemToChange.getAvailable()) {
            itemToChange.setAvailable(item.getAvailable());
        }

        log.debug("Данные вещи пользователя успешно обновлены.");
        return itemToChange;
    }

    // Получаем данные вещи по ее id
    @Override
    public Item getItemById(Long userId, Long itemId) {
        log.debug("Получаем данные вещи пользователя по её id.");
        return items.get(userId).stream()
                .filter(item -> Objects.equals(itemId, item.getId()))
                .findFirst()
                .orElseThrow(() -> new ItemNotFoundException("Вещь с id " + itemId + " у пользователя с id " + userId +
                        " не найдена."));
    }

    // Получаем все вещи пользователя
    @Override
    public Collection<Item> getAllUserItems(Long userId) {
        log.debug("Получаем данные о всех вещах пользователя.");
        return items.get(userId);
    }

    // Ищем вещи, содержащие в названии или описании переданный текст
    @Override
    public Collection<Item> searchItem(String text) {
        log.debug("Ищем вещи, содержащие в названии или описании переданный текст.");
        if (text != null && !text.isBlank()) {
            return items.values().stream()
                    .flatMap(Collection::stream)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(Item::getAvailable)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
