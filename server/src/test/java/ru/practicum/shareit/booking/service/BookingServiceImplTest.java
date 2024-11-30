package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ForbiddenAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    UserService userService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    private final User user = new User(
            1L,
            "Linda",
            "linda@gmail.com"
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

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusMinutes(5),
            item,
            user,
            BookingStatuses.WAITING

    );

    private final NewRequestBookingDto newRequestBookingDto = new NewRequestBookingDto(
            1L,
            booking.getStart(),
            booking.getEnd()
    );

    @Test
    void createBookingTest() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto createdBooking = bookingService.create(user.getId(), newRequestBookingDto);

        assertEquals(createdBooking, BookingMapper.mapBookingToBookingDto(booking));
    }

    @Test
    void createBookingWhenNotAvailableThenThrowValidationExceptionTest() {
        when(userService.getUserById(anyLong())).thenReturn(user);

        Item changedItem = new Item(
                1L,
                "Газонокосилка",
                "Косит газон",
                false,
                user,
                itemRequest
        );
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(changedItem));

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), newRequestBookingDto));

        assertEquals(validationException.getMessage(), "Предмет не доступен для бронирования.");
    }

    @Test
    void createBookingWhenStartEqualsEndThenThrowValidationExceptionTest() {
        LocalDateTime now = LocalDateTime.now();
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        NewRequestBookingDto changedRequestBookingDto = new NewRequestBookingDto(
                1L,
                now,
                now
        );

        ValidationException validationException = assertThrows(ValidationException.class,
                () -> bookingService.create(user.getId(), changedRequestBookingDto));

        assertEquals(validationException.getMessage(),
                "Дата начала бронирования и дата окончания не могут быть равны.");
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking changedBooking = new Booking(
                1L,
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                BookingStatuses.APPROVED

        );

        BookingDto result = bookingService.approve(user.getId(), booking.getId(), true);

        assertEquals(result, BookingMapper.mapBookingToBookingDto(changedBooking));
    }

    @Test
    void approveBookingWhenUserIdNotEqualsOwnerIdThenThrowForbiddenAccessExceptionTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ForbiddenAccessException forbiddenAccessException = assertThrows(ForbiddenAccessException.class,
                () -> bookingService.approve(0L, booking.getId(), true));

        assertEquals(forbiddenAccessException.getMessage(), "Ошибка при обработке запроса на бронирование. " +
                        "Подтвердить или отклонить запрос может только владелец вещи.");
    }

    @Test
    void approveBookingWhenRejectedTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking changedBooking = new Booking(
                1L,
                booking.getStart(),
                booking.getEnd(),
                item,
                user,
                BookingStatuses.REJECTED

        );

        BookingDto result = bookingService.approve(user.getId(), booking.getId(), false);

        assertEquals(result, BookingMapper.mapBookingToBookingDto(changedBooking));
    }

    @Test
    void approveWhenBookingNotFoundThenThrowNotFoundExceptionTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approve(user.getId(), booking.getId(), true));

        assertEquals(notFoundException.getMessage(), String.format("Ошибка при обработке запроса на бронирование. " +
                "Бронирование с id %d не найдено.", booking.getId()));
    }

    @Test
    void getBookingByIdTest() {
        when(userService.getUserById(anyLong())).thenReturn(user);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBookingById(user.getId(), booking.getId());

        assertEquals(result, BookingMapper.mapBookingToBookingDto(booking));
    }

    @Test
    void getBookingByIdWhenNotFoundThenThrowNotFoundExceptionTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(user.getId(), booking.getId()));

        assertEquals(notFoundException.getMessage(), String.format("Ошибка при обработке запроса на бронирование. " +
                "Бронирование с id %d не найдено.", booking.getId()));
    }

    @Test
    void getBookingByIdWhenWrongUserIdThenThrowForbiddenAccessExceptionTest() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        ForbiddenAccessException forbiddenAccessException = assertThrows(ForbiddenAccessException.class,
                () -> bookingService.getBookingById(0L, booking.getId()));

        assertEquals(forbiddenAccessException.getMessage(), "Получить доступ к данным бронирования может " +
                "только забронировавший или владелец вещи.");
    }

    @Test
    void findAllBookingsByUserWithStateALLTest() {
        when(bookingRepository.findAllByBookerId(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "ALL").stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByUserWithStatePASTTest() {
        when(bookingRepository.findAllUserBookingsInPast(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "PAST")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByUserWithStateCURRENTTest() {
        when(bookingRepository.findAllUserBookingsInPresent(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "CURRENT")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByUserWithStateFUTURETest() {
        when(bookingRepository.findAllUserBookingsInFuture(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "FUTURE")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByUserWithStateWAITINGTest() {
        when(bookingRepository.findAllByBookerIdAndStatus(user.getId(), BookingStatuses.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "WAITING")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByUserWithStateREJECTEDTest() {
        when(bookingRepository.findAllByBookerIdAndStatus(user.getId(), BookingStatuses.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByUser(user.getId(), "REJECTED")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStateALLTest() {
        when(bookingRepository.findAllOwnerBookings(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "ALL")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStatePASTTest() {
        when(bookingRepository.findAllOwnerBookingsInPast(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "PAST")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStateCURRENTTest() {
        when(bookingRepository.findAllOwnerBookingsInPresent(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "CURRENT")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStateFUTURETest() {
        when(bookingRepository.findAllOwnerBookingsInFuture(anyLong())).thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "FUTURE")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStateWAITINGTest() {
        when(bookingRepository.findAllOwnerBookingsByIdAndStatus(user.getId(), BookingStatuses.WAITING))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "WAITING")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }

    @Test
    void findAllBookingsByOwnerWithStateREJECTEDTest() {
        when(bookingRepository.findAllOwnerBookingsByIdAndStatus(user.getId(), BookingStatuses.REJECTED))
                .thenReturn(List.of(booking));

        List<BookingDto> result = bookingService.findAllBookingsByOwner(user.getId(), "REJECTED")
                .stream().toList();

        assertEquals(result, List.of(BookingMapper.mapBookingToBookingDto(booking)));
    }
}