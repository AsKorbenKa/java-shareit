package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface InMemoryUserStorage {
    User create(User user);

    User update(Long userId, UserDto user);

    User getUserById(Long userId);

    void delete(Long userId);
}
