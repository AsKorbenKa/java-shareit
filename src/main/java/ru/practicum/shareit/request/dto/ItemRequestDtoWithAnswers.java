package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
public class ItemRequestDtoWithAnswers {
    Long id;
    @NotBlank(message = "Описание запроса не может быть пустым.")
    String description;
    Long requester;
    LocalDateTime created;
    Collection<ItemDtoShort> items;
}
