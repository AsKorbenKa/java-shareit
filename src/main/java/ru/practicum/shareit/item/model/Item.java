package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    Long id;
    @NotBlank(message = "Название вещи не может быть пустым.")
    String name;
    @NotBlank(message = "Описание вещи не может быть пустым.")
    String description;
    @NotNull
    Boolean available;
}
