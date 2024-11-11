package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
        user.getId(),
        user.getName(),
        user.getEmail()
        );
    }

    public static User mapUserDtoToUser(Long userId, UserDto userDto) {
        User user = new User();
        user.setId(userId);
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static User updateUserData(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }

        return user;
    }
}
