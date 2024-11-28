package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class NewRequestBookingDtoTest {
    private final JacksonTester<NewRequestBookingDto> json;
    LocalDateTime end = LocalDateTime.now();
    LocalDateTime start = end.minusWeeks(1);

    @Test
    void testNewRequestBookingDto() throws Exception {
        NewRequestBookingDto bookingDto = new NewRequestBookingDto(
                1L,
                start,
                end
        );

        JsonContent<NewRequestBookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
    }

}