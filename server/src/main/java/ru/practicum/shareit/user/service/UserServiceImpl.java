package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User create(UserDto user) {
        log.debug("Добавляем нового пользователя в базу данных.");
        return userRepository.save(UserMapper.mapUserDtoToUser(null, user));
    }

    @Override
    @Transactional
    public User update(Long userId, UserDto userDto) {
        log.debug("Обновляем данные о пользователе.");
        // Проверяем существует ли пользователь
        User user = getUserById(userId);

        // Проверяем существует ли email
        Optional<User> userToCheck = userRepository.findByEmail(userDto.getEmail());
        if (userToCheck.isPresent() && !userToCheck.get().getEmail().equals(user.getEmail())) {
            throw new DuplicatedDataException("Ошибка при обновлении данных пользователя. Email уже существует.");
        }

        log.debug("Данные о пользователе были успешно обновлены.");
        return userRepository.save(UserMapper.updateUserData(user, userDto));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        log.debug("Получаем данные пользователя по его id.");
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        log.debug("Удаляем пользователя из базы данных.");
        userRepository.deleteById(userId);
    }

}
