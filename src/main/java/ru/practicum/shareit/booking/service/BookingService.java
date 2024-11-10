package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, NewRequestBookingDto bookingDto);

    BookingDto approve(Long userId, Long bookingId, boolean approved);

    BookingDto getBookingById(Long userId, Long bookingId);

    Collection<BookingDto> findAllBookingsByUser(Long userId, String bookingState);

    Collection<BookingDto> findAllBookingsByOwner(Long userId, String bookingState);
}
