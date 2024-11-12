package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ImprovedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ImprovedItemDto getItemById(Long itemId);

    Collection<ImprovedItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItem(String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
