package ru.practicum.shareit.request.dto;

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
class ItemRequestDtoWithAnswersTest {
    private final JacksonTester<ItemRequestDtoWithAnswers> json;

    @Test
    void testItemRequestDto() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.now();

        ItemRequestDtoWithAnswers requestDtoWithAnswers = new ItemRequestDtoWithAnswers(
                1L,
                "Срочно нужна бензопила.",
                11L,
                localDateTime,
                Collections.emptyList()
        );

        JsonContent<ItemRequestDtoWithAnswers> result = json.write(requestDtoWithAnswers);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Срочно нужна бензопила.");
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(11);
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(localDateTime.toString());
        assertThat(result).extractingJsonPathArrayValue("$.items").isEqualTo(Collections.emptyList());
    }
}