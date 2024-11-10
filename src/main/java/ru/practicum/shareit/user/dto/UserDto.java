package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @NotBlank(message = "Имя пользователя должно быть указано.")
    String name;
    @NotBlank(message = "Email пользователя должен быть указан.")
    @Email(message = "Email должен быть в формате user@yandex.ru.")
    String email;
}
