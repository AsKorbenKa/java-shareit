package ru.practicum.shareit.item.dto;

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
class CommentDtoTest {
    private final JacksonTester<CommentDto> json;
    LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = new CommentDto(
                1L,
                11L,
                "Джонатан Питч",
                "Секатор сломался при первом же использовании",
                localDateTime
        );

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Джонатан Питч");
        assertThat(result).extractingJsonPathStringValue("$.text")
                .isEqualTo("Секатор сломался при первом же использовании");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(localDateTime.toString());
    }
}