package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewRequestBookingDto {
    @NotNull(message = "Id предмета не может быть равен null.")
    Long itemId;

    @NotNull(message = "Дата начала бронирования не может равна null.")
    @FutureOrPresent(message = "Начальные даты и время бронирования не могут быть в прошлом.")
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может равна null.")
    @Future(message = "Окончание бронирования не может быть в настоящем или прошлом.")
    LocalDateTime end;
}
