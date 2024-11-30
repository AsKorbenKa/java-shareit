package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

public class ItemRequestMapper {
    public static ItemRequest mapRequestDtoToRequest(User user, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());

        return itemRequest;
    }

    public static ItemRequestDtoWithAnswers mapRequestToRequestDtoWithAnswers(ItemRequest itemRequest,
                                                                              Collection<ItemDtoShort> items) {
        return new ItemRequestDtoWithAnswers(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated(),
                items
        );
    }

    public static ItemRequestDto mapRequestToRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester().getId(),
                itemRequest.getCreated()
        );
    }
}
