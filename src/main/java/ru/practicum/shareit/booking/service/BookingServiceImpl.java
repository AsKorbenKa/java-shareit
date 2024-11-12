package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;
import ru.practicum.shareit.booking.enums.BookingStates;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    ItemRepository itemRepository;
    UserService userService;
    BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(ItemRepository repository,
                              UserService userService,
                              BookingRepository bookingRepository) {
        this.itemRepository = repository;
        this.userService = userService;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, NewRequestBookingDto bookingDto) {
        log.debug("Создаем новую запись о бронировании.");
        // Проверяем существует ли пользователь
        User user = userService.getUserById(userId);

        // Проверяем существует ли предмет
        Item item = isItemExists(bookingDto.getItemId());
        Booking booking;

        if (!item.getAvailable()) {
            throw new ValidationException("Предмет не доступен для бронирования.");
        }

        if (bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            booking = bookingRepository.save(BookingMapper.mapNewRequestToBooking(bookingDto, user, item));
        } else {
            throw new ValidationException("Дата начала бронирования и дата окончания не могут быть равны.");
        }
        log.debug("Новая бронь была успешно добавлена.");
        return BookingMapper.mapBookingToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, boolean approved) {
        log.debug("Одобряем или отклоняем запрос на бронирование.");

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при обработке запроса на бронирование. " +
                "Бронирование с id %d не найдено.", bookingId)));

        if (!userId.equals(booking.getItem().getUser().getId())) {
            throw new ForbiddenAccessException("Ошибка при обработке запроса на бронирование. " +
                    "Подтвердить или отклонить запрос может только владелец вещи.");
        } else {
            if (approved) {
                booking.setStatus(BookingStatuses.APPROVED);
            } else {
                booking.setStatus(BookingStatuses.REJECTED);
            }
        }
        bookingRepository.save(booking);

        log.debug("Владелец вещи успешно вынес решение по бронированию.");
        return BookingMapper.mapBookingToBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        log.debug("Получаем данные об определенном бронировании.");
        // Проверяем существует ли пользователь
        userService.getUserById(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Ошибка при обработке запроса на бронирование. " +
                        "Бронирование с id %d не найдено.", bookingId)));

        if (userId.equals(booking.getItem().getUser().getId()) || userId.equals(booking.getBooker().getId())) {
            log.debug("Успешно получена запись об определенном бронировании.");
            return BookingMapper.mapBookingToBookingDto(booking);
        } else {
            throw new ForbiddenAccessException("Получить доступ к данным бронирования может только " +
                    "забронировавший или владелец вещи.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByUser(Long userId, String bookingState) {
        log.debug("Получаем все записи о бронировании определенного пользователя.");
        BookingStates state = BookingStates.valueOf(bookingState);
        Collection<Booking> userBookingsList;

        // Проверяем существует ли пользователь
        userService.getUserById(userId);

        userBookingsList = switch (state) {
            case ALL -> bookingRepository.findAllByBookerId(userId);
            case PAST -> bookingRepository.findAllUserBookingsInPast(userId);
            case CURRENT -> bookingRepository.findAllUserBookingsInPresent(userId);
            case FUTURE -> bookingRepository.findAllUserBookingsInFuture(userId);
            case WAITING -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatuses.WAITING);
            case REJECTED -> bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatuses.REJECTED);
        };

        log.debug("Все записи о бронировании определенного пользователя успешно получены.");
        return userBookingsList.stream()
                .map(BookingMapper::mapBookingToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> findAllBookingsByOwner(Long userId, String bookingState) {
        log.debug("Получаем все записи о забронированных вещах владельца.");
        BookingStates state = BookingStates.valueOf(bookingState);
        Collection<Booking> ownerBookingsList;

        // Проверяем существует ли пользователь
        userService.getUserById(userId);

        ownerBookingsList = switch (state) {
            case ALL -> bookingRepository.findAllOwnerBookings(userId);
            case PAST -> bookingRepository.findAllOwnerBookingsInPast(userId);
            case CURRENT -> bookingRepository.findAllOwnerBookingsInPresent(userId);
            case FUTURE -> bookingRepository.findAllOwnerBookingsInFuture(userId);
            case WAITING -> bookingRepository.findAllOwnerBookingsByIdAndStatus(userId, BookingStatuses.WAITING);
            case REJECTED -> bookingRepository.findAllOwnerBookingsByIdAndStatus(userId, BookingStatuses.REJECTED);
        };

        log.debug("Все записи о забронированных вещах владельца успешно получены.");
        return ownerBookingsList.stream()
                .map(BookingMapper::mapBookingToBookingDto)
                .sorted(Comparator.comparing(BookingDto::getStart))
                .collect(Collectors.toList());
    }

    private Item isItemExists(Long itemId) {
        // Проверяем существует ли предмет
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + itemId + " не найден."));
    }
}
