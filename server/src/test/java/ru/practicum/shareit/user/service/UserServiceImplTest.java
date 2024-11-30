package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicatedDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserDto userDto = new UserDto(
            1L,
            "Gorge",
            "gorge@gmail.com"
    );

    @Test
    void createUserTest() {
        User newUser = new User(null, "Gorge", "gorge@gmail.com");
        when(userRepository.save(newUser)).thenReturn(newUser);

        User savedUser = userService.create(userDto);

        assertEquals(newUser, savedUser);
        verify(userRepository).save(newUser);
    }

    @Test
    void updateUserTest() {
        User newUser = new User(1L, "Gorge", "gorge@gmail.com");
        when(userService.create(userDto)).thenReturn(newUser);
        User savedUser = userService.create(userDto);

        UserDto changedUser = new UserDto(
                null,
                "Margo",
                "margo@gmail.com"
        );

        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(newUser));
        when(userService.update(savedUser.getId(), changedUser)).thenReturn(
                UserMapper.mapUserDtoToUser(savedUser.getId(), changedUser));

        User updatedUser = userService.update(savedUser.getId(), changedUser);

        assertNotNull(updatedUser);
        assertEquals("Margo", updatedUser.getName());
        assertEquals("margo@gmail.com", updatedUser.getEmail());
    }

    @Test
    void updateUserWhenEmailExistsThenThrowDuplicatedDataExceptionTest() {
        User newUser = new User(1L, "Gorge", "gorge@gmail.com");
        User changedUser = new User(1L, "Margo", "margo@gmail.com");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(newUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(changedUser));

        DuplicatedDataException duplicatedDataException = assertThrows(DuplicatedDataException.class,
                () -> userService.update(newUser.getId(), userDto));

        assertEquals(duplicatedDataException.getMessage(), "Ошибка при обновлении данных пользователя. " +
                "Email уже существует.");
    }

    @Test
    void getUserByIdWhenFountTest() {
        User newUser = new User(1L, "Gorge", "gorge@gmail.com");
        when(userService.create(userDto)).thenReturn(newUser);
        User savedUser = userService.create(userDto);

        when(userRepository.findById(savedUser.getId())).thenReturn(Optional.of(newUser));

        User userToCheck = userService.getUserById(savedUser.getId());

        assertEquals(newUser, userToCheck);
    }

    @Test
    void getUserByIdWhenNotFountTest() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));

        assertEquals(userNotFoundException.getMessage(), "Пользователь с id " + userId + " не найден.");
    }

    @Test
    void delete() {
        long userId = 0L;
        userService.delete(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}