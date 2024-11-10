package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserService {
    User create(UserDto user);

    User update(Long userId, UserDto user);

    User getUserById(Long userId);

    void delete(Long userId);
}
