package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static Booking mapNewRequestToBooking(NewRequestBookingDto bookingDto, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatuses.WAITING);

        return booking;
    }

    public static BookingDto mapBookingToBookingDto(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            ItemMapper.mapToItemDto(booking.getItem()),
            UserMapper.mapToUserDto(booking.getBooker()),
            booking.getStatus()
        );
    }
}
