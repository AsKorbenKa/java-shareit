package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ImprovedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemMapper {
    public static ItemDto mapToItemDto(Item item) {
        Long requestId;

        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        } else {
            requestId = null;
        }
        return new ItemDto(
        item.getId(),
        item.getUser().getId(),
        item.getName(),
        item.getDescription(),
        item.getAvailable(),
        requestId
        );
    }

    public static Item mapToItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setUser(user);
        item.setRequest(itemRequest);
        return item;
    }

    public static Item updateItemData(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            item.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }

        return item;
    }

    public static ImprovedItemDto mapItemToImprovedItemDto(Item item, LocalDateTime lastBookingDate,
                                                           LocalDateTime nextBookingDate, List<CommentDto> comments) {
        ImprovedItemDto itemDto = new ImprovedItemDto();
        itemDto.setId(item.getId());
        itemDto.setUserId(item.getUser().getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setLastBooking(lastBookingDate);
        itemDto.setNextBooking(nextBookingDate);
        itemDto.setComments(comments);

        return itemDto;
    }
}
