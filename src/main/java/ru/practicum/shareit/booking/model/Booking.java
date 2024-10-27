package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;

@Data
public class Booking {
    Long id;
    LocalDateTime start = LocalDateTime.now();
    LocalDateTime end;
    Long item;
    Long booker;
    BookingStatus status;
}
