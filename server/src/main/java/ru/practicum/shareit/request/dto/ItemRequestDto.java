package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    Long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    String description;
    Long requester;
    LocalDateTime created;
}