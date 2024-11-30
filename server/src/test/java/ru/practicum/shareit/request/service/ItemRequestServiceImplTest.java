package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User author = new User(
            1L,
            "Gorge",
            "gorge@gmail.com"
    );

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            null,
            "Просьба добавить большие садовые ножницы.",
            null,
            null
    );

    private final ItemRequest itemRequest = new ItemRequest(
            1L,
            "Просьба добавить металлоискатель.",
            author,
            LocalDateTime.now()
    );

    @Test
    void createRequestTest() {
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto createdRequest = requestService.create(itemRequestDto, author.getId());

        assertEquals(createdRequest, ItemRequestMapper.mapRequestToRequestDto(itemRequest));
    }

    @Test
    void getAllUserRequestsTest() {
        List<ItemRequestDtoWithAnswers> requests = List.of(ItemRequestMapper
                .mapRequestToRequestDtoWithAnswers(itemRequest, Collections.emptyList()));
        when(userRepository.findById(author.getId())).thenReturn(Optional.of(author));
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDtoWithAnswers> userRequests = requestService.getAllUserRequests(author.getId());

        assertEquals(requests, userRequests);
    }

    @Test
    void getAllRequestsTest() {
        List<ItemRequestDto> requests = List.of(ItemRequestMapper.mapRequestToRequestDto(itemRequest));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyLong())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> allRequests = requestService.getAllRequests(author.getId());

        assertEquals(requests, allRequests);
    }

    @Test
    void getRequestByIdTest() {
        ItemRequestDtoWithAnswers request = ItemRequestMapper
                .mapRequestToRequestDtoWithAnswers(itemRequest, Collections.emptyList());
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestDtoWithAnswers requestById = requestService.getRequestById(itemRequest.getId());

        assertEquals(request, requestById);
    }
}