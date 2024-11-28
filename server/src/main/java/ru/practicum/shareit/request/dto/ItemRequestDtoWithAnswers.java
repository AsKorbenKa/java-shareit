package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoShort;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemRequestDtoWithAnswers {
    Long id;
    String description;
    Long requester;
    LocalDateTime created;
    Collection<ItemDtoShort> items;
}
