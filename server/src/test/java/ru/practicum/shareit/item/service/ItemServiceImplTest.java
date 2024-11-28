package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ImprovedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private final User user = new User(
            1L,
            "Gorge",
            "gorge@gmail.com"
    );

    private final ItemDto itemDto = new ItemDto(
            null,
            null,
            "Швейная машинка",
            "Строчит так, как никто",
            true,
            1L
    );

    private final ItemRequest itemRequest = new ItemRequest(
            1L,
            "Просьба добавить металлоискатель.",
            null,
            LocalDateTime.now()
    );

    private final Item item = new Item(
            1L,
            "Газонокосилка",
            "Косит газон",
            true,
            user,
            itemRequest
    );

    private final Comment comment = new Comment(
            1L,
            item,
            user,
            "текст",
            LocalDateTime.now()
            );

    @Test
    void createItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto createdItem = itemService.create(user.getId(), itemDto);

        assertEquals(createdItem, ItemMapper.mapToItemDto(item));
    }

    @Test
    void createItemWhenRequestNotFoundThenThrowNotFoundExceptionTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.create(user.getId(), itemDto));

        assertEquals(notFoundException.getMessage(), String.format("Ошибка при добавлении предмета. " +
                "Запрос на добавление с id %d не найден.", itemDto.getRequestId()));
    }

    @Test
    void updateItemTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        Item changedItem = new Item(
                1L,
                "Робот-пылесос",
                "Убирается в вашем доме",
                true,
                user,
                itemRequest
        );

        when(itemRepository.save(any(Item.class))).thenReturn(changedItem);

        ItemDto updatedItem = itemService.update(user.getId(), item.getId(), ItemMapper.mapToItemDto(changedItem));

        assertNotNull(updatedItem);
        assertEquals(changedItem.getName(), updatedItem.getName());
        assertEquals(changedItem.getDescription(), updatedItem.getDescription());
    }

    @Test
    void getItemByIdTest() {
        LocalDateTime last = LocalDateTime.now().minusHours(1);
        LocalDateTime next = last.plusHours(5);
        ImprovedItemDto improvedItemDto = ItemMapper.mapItemToImprovedItemDto(item, last, next,
                List.of(CommentMapper.mapCommentToCommentDto(comment)));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingEndByItemId(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singleton(last));
        when(bookingRepository.findNextBookingStartByItemId(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singleton(next));

        ImprovedItemDto itemToCheck = itemService.getItemById(item.getId());

        assertEquals(improvedItemDto, itemToCheck);
    }

    @Test
    void getAllUserItemsTest() {
        LocalDateTime last = LocalDateTime.now().minusHours(1);
        LocalDateTime next = last.plusHours(5);
        ImprovedItemDto improvedItemDto = ItemMapper.mapItemToImprovedItemDto(item, last, next,
                List.of(CommentMapper.mapCommentToCommentDto(comment)));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByUserId(anyLong())).thenReturn(List.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));
        when(bookingRepository.findLastBookingEndByItemId(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singleton(last));
        when(bookingRepository.findNextBookingStartByItemId(anyLong(), any(LocalDateTime.class)))
                .thenReturn(Collections.singleton(next));

        List<ImprovedItemDto> result = itemService.getAllUserItems(user.getId()).stream().toList();

        assertEquals(result, List.of(improvedItemDto));
    }

    @Test
    void searchItemTest() {
        ItemDto itemForSearch = ItemMapper.mapToItemDto(item);

        when(itemRepository.findAllByText(anyString())).thenReturn(List.of(item));

        List<ItemDto> items = itemService.searchItem("text").stream().toList();

        assertEquals(items, List.of(itemForSearch));
    }

    @Test
    void createCommentTest() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = CommentMapper.mapCommentToCommentDto(comment);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto createdComment = itemService.createComment(user.getId(), item.getId(), commentDto);

        assertEquals(createdComment, commentDto);
    }

    @Test
    void createCommentWithUserWithoutBookingThenThrowValidationException() {
        CommentDto commentDto = CommentMapper.mapCommentToCommentDto(comment);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndEndBefore(anyLong(), anyLong(),
                any(LocalDateTime.class))).thenReturn(false);

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> itemService.createComment(user.getId(), item.getId(), commentDto));

        assertEquals(validationException.getMessage(), String.format("Ошибка при создании комментария. " +
                "Пользователь c id %d никогда не бронировал вещь с id %d.", user.getId(), item.getId()));
    }
}