package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ImprovedItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;

    private final User user = new User(1L, "Gorge", "gorge@yandex.ru");

    private final ItemDto itemDto = new ItemDto(
            1L,
            1L,
            "name",
            "description",
            true,
            1L
    );

    private final CommentDto commentDto = new CommentDto(
            1L,
            1L,
            "Gorge",
            "some text",
            LocalDateTime.now()
    );

    private final ImprovedItemDto improvedItemDto = new ImprovedItemDto(
            1L,
            1L,
            "some name",
            "some description",
            true,
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(commentDto)
    );

    @Test
    @SneakyThrows
    void createItemTest() {
        when(itemService.create(user.getId(), itemDto)).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void createCommentTest() {
        when(itemService.createComment(user.getId(), itemDto.getId(), commentDto)).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", itemDto.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.itemId", is(commentDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
    }

    @Test
    @SneakyThrows
    void updateItemTest() {
        ItemDto changedItemDto = new ItemDto(
                1L,
                1L,
                "another name",
                "another description",
                false,
                1L
        );
        when(itemService.update(user.getId(), itemDto.getId(), changedItemDto)).thenReturn(changedItemDto);

        mvc.perform(patch("/items/{itemId}", changedItemDto.getId())
                        .content(mapper.writeValueAsString(changedItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(changedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(changedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(changedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(changedItemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getItemByIdTest() {
        when(itemService.getItemById(itemDto.getId())).thenReturn(improvedItemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(improvedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(improvedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(improvedItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(improvedItemDto.getAvailable())));
    }

    @Test
    @SneakyThrows
    void getAllUserItemsTest() {
        when(itemService.getAllUserItems(user.getId())).thenReturn(List.of(improvedItemDto));

        String result = mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(improvedItemDto)), result);
    }

    @Test
    @SneakyThrows
    void searchItemTest() {
        when(itemService.searchItem(anyString())).thenReturn(List.of(itemDto));

        String result = mvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(itemDto)), result);
    }
}