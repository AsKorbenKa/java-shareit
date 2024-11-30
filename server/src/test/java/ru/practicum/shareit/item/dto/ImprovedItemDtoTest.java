package ru.practicum.shareit.item.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ImprovedItemDtoTest {
    private final JacksonTester<ImprovedItemDto> json;


    @Test
    void testImprovedItemDto() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();

        ImprovedItemDto itemDto = new ImprovedItemDto(
                1L,
                11L,
                "Секатор",
                "Срежет лишние ветки",
                true,
                localDateTime,
                localDateTime,
                Collections.emptyList()
        );

        JsonContent<ImprovedItemDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.userId").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Секатор");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Срежет лишние ветки");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.comments").isEqualTo(Collections.emptyList());
    }
}