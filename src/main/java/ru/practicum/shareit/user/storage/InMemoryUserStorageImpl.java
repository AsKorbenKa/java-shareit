package ru.practicum.shareit.user.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.DuplicatedUserDataException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class InMemoryUserStorageImpl implements InMemoryUserStorage {
    // используем счетчик для установки id пользователей
    Long counter = 0L;
    final List<User> users = new ArrayList<>();

    // Создаем нового пользователя
    @Override
    public User create(User user) {
        log.debug("Создаем нового пользователя.");
        if (isUserWithSameEmailExists(user.getEmail())) {
            throw new DuplicatedUserDataException("Ошибка при попытке создать нового пользователя. " +
                    "Пользователь с email " + user.getEmail() + " уже существует.");
        } else {
            user.setId(counter += 1);
            users.add(user);
        }

        log.debug("Новый пользователь был успешно добавлен.");
        return user;
    }

    // Обновляем данные пользователя
    @Override
    public User update(Long userId, UserDto user) {
        log.debug("Обновляем данные пользователя.");
        // Проверяем существует ли пользователь
        User userToChange = getUserById(userId);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (!userToChange.getEmail().equals(user.getEmail()) && isUserWithSameEmailExists(user.getEmail())) {
                throw new DuplicatedUserDataException("Ошибка при попытке создать нового пользователя. " +
                        "Пользователь с email " + user.getEmail() + " уже существует.");
            } else {
                userToChange.setEmail(user.getEmail());
            }
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            userToChange.setName(user.getName());
        }

        log.debug("Данные пользователя были успешно обновлены.");
        return userToChange;
    }

    // Получаем данные пользователя по его id
    @Override
    public User getUserById(Long userId) {
        log.debug("Получаем данные пользователя по его id.");
        return users.stream()
                .filter(user -> Objects.equals(userId, user.getId()))
                .findAny()
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    // удаляем пользователя
    @Override
    public void delete(Long userId) {
        log.debug("Удаляем пользователя.");
        users.remove(users.stream().filter(user -> userId.equals(user.getId())).findFirst()
                .orElseThrow());
    }

    private boolean isUserWithSameEmailExists(String email) {
        log.debug("Проверяем существует ли пользователь с указанным email.");
        Optional<User> userInList = users.stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst();

        if (userInList.isPresent()) {
            return true;
        } else {
            return false;
        }
    }
}
