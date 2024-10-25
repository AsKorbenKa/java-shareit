package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

@Service
@RequiredArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    InMemoryUserStorage inMemoryUserStorage;

    public User create(User user) {
        return inMemoryUserStorage.create(user);
    }

    public User update(Long userId, UserDto user) {
        return inMemoryUserStorage.update(userId, user);
    }

    public User getUserById(Long userId) {
        return inMemoryUserStorage.getUserById(userId);
    }

    public void delete(Long userId) {
        inMemoryUserStorage.delete(userId);
    }
}
