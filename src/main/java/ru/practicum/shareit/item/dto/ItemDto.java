package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    Long userId;
    @NotBlank(message = "Имя предмета не может быть пустым.")
    String name;
    @NotBlank(message = "Описание предмета не может быть пустым.")
    String description;
    @NotNull(message = "Параметр доступности предмета не может быть равен null.")
    Boolean available;
}
