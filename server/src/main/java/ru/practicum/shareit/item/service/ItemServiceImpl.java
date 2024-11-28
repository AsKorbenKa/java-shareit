package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ImprovedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.NotFoundException;
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
import java.util.*;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemServiceImpl(ItemRepository repository,
                           UserRepository userRepository,
                           BookingRepository bookingRepository,
                           CommentRepository commentRepository,
                           ItemRequestRepository itemRequestRepository) {
        this.itemRepository = repository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.debug("Создаем новую запись о предмете.");
        ItemRequest itemRequest;
        // Проверяем существует ли пользователь
        User user = isUserExists(userId);
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Ошибка при добавлении предмета. " +
                            "Запрос на добавление с id %d не найден.", itemDto.getRequestId())));
        } else {
            itemRequest = null;
        }

        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto, user, itemRequest));

        log.debug("Новая запись о предмете успешно добавлена в базу данных.");
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.debug("Обновляем данные о предмете.");
        // Проверяем существует ли пользователь
        isUserExists(userId);

        // Проверяем существует ли предмет
        Item item = isItemExists(itemId);

        Item updatedItem = itemRepository.save(ItemMapper.updateItemData(item, itemDto));

        log.debug("Данные о предмете успешно обновлены.");
        return ItemMapper.mapToItemDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ImprovedItemDto getItemById(Long itemId) {
        log.debug("Получаем данные о предмете по его id.");
        // Проверяем существует ли предмет
        Item item = isItemExists(itemId);
        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::mapCommentToCommentDto)
                .toList();

        log.debug("Данные о предмете по его id успешно получены.");
        return ItemMapper.mapItemToImprovedItemDto(item, getLastBookingEndDate(itemId), getNextBookingStartDate(itemId),
                comments);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ImprovedItemDto> getAllUserItems(Long userId) {
        log.debug("Получаем список всех вещей пользователя.");
        // Проверяем существует ли пользователь
        isUserExists(userId);

        return itemRepository.findAllByUserId(userId).stream()
                .map(item -> {
                    List<CommentDto> comments = commentRepository.findAllByItemId(item.getId()).stream()
                            .map(CommentMapper::mapCommentToCommentDto)
                            .toList();
                    LocalDateTime lastBookingDate = getLastBookingEndDate(item.getId());
                    LocalDateTime nextBookingDate = getNextBookingStartDate(item.getId());
                    return ItemMapper.mapItemToImprovedItemDto(item, lastBookingDate, nextBookingDate,
                            comments);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> searchItem(String text) {
        log.debug("Ищем предметы по краткому описанию.");
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByText(text).stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        log.debug("Добавляем новый комментарий в базу данных.");
        // Проверяем существует ли пользователь и предмет
        User user = isUserExists(userId);
        Item item = isItemExists(itemId);

        // Проверяем бронировал ли этот пользователь эту вещь
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId,
                LocalDateTime.now())) {
            throw new ValidationException(String.format("Ошибка при создании комментария. " +
                    "Пользователь c id %d никогда не бронировал вещь с id %d.", userId, itemId));
        }

        Comment comment = commentRepository.save(CommentMapper.mapCommentDtoToComment(user, item, commentDto));
        log.debug("Новый комментарий в базу данных был успешно добавлен.");
        return CommentMapper.mapCommentToCommentDto(comment);
    }

    private LocalDateTime getLastBookingEndDate(Long itemId) {
        LocalDateTime localDateTime = bookingRepository.findLastBookingEndByItemId(itemId, LocalDateTime.now())
                .stream()
                .max(Comparator.naturalOrder())
                .orElse(null);

        if (localDateTime != null && localDateTime.isAfter(LocalDateTime.now().minusSeconds(5))) {
            return null;
        }

        return localDateTime;
    }

    private LocalDateTime getNextBookingStartDate(Long itemId) {
        return bookingRepository.findNextBookingStartByItemId(itemId, LocalDateTime.now())
                .stream()
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private User isUserExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден."));
    }

    private Item isItemExists(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
    }
}
