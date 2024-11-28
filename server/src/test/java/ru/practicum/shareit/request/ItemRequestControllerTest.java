package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswers;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService requestService;

    private final User user = new User(1L, "Gorge", "gorge@yandex.ru");

    private final ItemRequestDto requestDto = new ItemRequestDto(
            1L,
            "text",
            1L,
            LocalDateTime.now()
    );

    private final ItemRequestDtoWithAnswers requestDtoWithAnswers = new ItemRequestDtoWithAnswers(
            1L,
            "text",
            1L,
            LocalDateTime.now(),
            Collections.emptyList()
    );

    @Test
    @SneakyThrows
    void createRequestTest() {
        when(requestService.create(requestDto, user.getId())).thenReturn(requestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())))
                .andExpect(jsonPath("$.requester", is(requestDto.getRequester()), Long.class));
    }

    @Test
    @SneakyThrows
    void getAllUserRequestsTest() {
        when(requestService.getAllUserRequests(user.getId())).thenReturn(List.of(requestDtoWithAnswers));

        String result = mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(requestDtoWithAnswers)), result);
    }

    @Test
    @SneakyThrows
    void getAllRequestsTest() {
        when(requestService.getAllRequests(user.getId())).thenReturn(List.of(requestDto));

        String result = mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(requestDto)), result);
    }

    @Test
    @SneakyThrows
    void getRequestById() {
        when(requestService.getRequestById(requestDto.getId())).thenReturn(requestDtoWithAnswers);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDtoWithAnswers.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDtoWithAnswers.getDescription())))
                .andExpect(jsonPath("$.requester", is(requestDtoWithAnswers.getRequester()), Long.class));
    }
}