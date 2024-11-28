package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingDtoTest {
    private final JacksonTester<BookingDto> json;
    LocalDateTime now = LocalDateTime.now();

    @Test
    void testBookingDto() throws Exception {
        ItemDto itemDto = new ItemDto(
                1L,
                1L,
                "Газонокосилка",
                "Косит газон",
                true,
                null
        );

        UserDto userDto = new UserDto(
                2L,
                "Диана",
                "diana@gmail.com"
        );

        BookingDto bookingDto = new BookingDto(
                1L,
                now,
                now,
                itemDto,
                userDto,
                BookingStatuses.APPROVED
        );

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(now.toString()
                .substring(0, 27));
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo(
                BookingStatuses.APPROVED.name());
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Газонокосилка");
        assertThat(result).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("diana@gmail.com");
    }
}