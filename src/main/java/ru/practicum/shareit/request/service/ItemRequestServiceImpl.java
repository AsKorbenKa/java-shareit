package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;
    ItemRepository itemRepository;

    @Autowired
    public ItemRequestServiceImpl(UserRepository userRepository, ItemRequestRepository itemRequestRepository,
                                  ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRequestRepository = itemRequestRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        log.debug("Создаем новую запись о запросе на добавление предмета.");
        // проверяем существует ли пользователь
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при добавлении запроса на добавление " +
                        "предмета. Пользователь с id %d не найден", userId)));

        ItemRequest itemRequest = itemRequestRepository.save(ItemRequestMapper.mapRequestDtoToRequest(user,
                itemRequestDto));

        log.debug("Новая запись о запросе на добавление предмета успешно добавлена.");
        return ItemRequestMapper.mapRequestToRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithAnswers> getAllUserRequests(Long userId) {
        log.debug("Получаем список всех запросов на добавление предмета определенного пользователя.");
        // проверяем существует ли пользователь
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при получении всех запросов на " +
                        "добавление предмета. Пользователь с id %d не найден", userId)));

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);

        log.debug("Список всех запросов определенного пользователя на добавление предмета успешно получен.");
        return requests.stream()
                .map(request -> {
                    Collection<ItemDtoShort> items = itemRepository.findAllByRequestId(request.getId());
                    return ItemRequestMapper.mapRequestToRequestDtoWithAnswers(request, items);
                }).toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        log.debug("Получаем список всех запросов на добавление предмета.");

        // проверяем существует ли пользователь
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при получении всех запросов на " +
                        "добавление предмета. Пользователь с id %d не найден", userId)));

        Collection<ItemRequest> requests = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId);

        log.debug("Список всех запросов на добавление предмета успешно получен.");
        return requests.stream().map(ItemRequestMapper::mapRequestToRequestDto).toList();
    }

    @Override
    public ItemRequestDtoWithAnswers getRequestById(Long requestId) {
        log.debug("Получаем запросов на добавление предмета по его id.");

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при получении запроса на добавление по " +
                        "его id. Запрос с id %d не найден.", requestId)));

        Collection<ItemDtoShort> requestAnswers = itemRepository.findAllByRequestId(requestId);

        log.debug("Запросов на добавление предмета успешно получен по его id.");
        return ItemRequestMapper.mapRequestToRequestDtoWithAnswers(request, requestAnswers);
    }
}
