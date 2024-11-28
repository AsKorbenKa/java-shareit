package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService bookingService;

    private final NewRequestBookingDto newRequestBookingDto = new NewRequestBookingDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
    );

    private final ItemDto itemDto = new ItemDto(
            1L,
            1L,
            "name",
            "description",
            true,
            1L
    );

    private final User user = new User(1L, "Gorge", "gorge@yandex.ru");

    private final UserDto userDto = new UserDto(1L, "Gorge", "gorge@yandex.ru");

    private final BookingDto bookingDto = new BookingDto(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            itemDto,
            userDto,
            BookingStatuses.WAITING
    );

    @Test
    @SneakyThrows
    void createBookingTest() {
        when(bookingService.create(user.getId(), newRequestBookingDto)).thenReturn(bookingDto);

        String result = mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(newRequestBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    @SneakyThrows
    void approveBookingTest() {
        when(bookingService.approve(user.getId(), bookingDto.getId(), true)).thenReturn(bookingDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    @SneakyThrows
    void getBookingByIdTest() {
        when(bookingService.getBookingById(user.getId(), bookingDto.getId())).thenReturn(bookingDto);

        String result = mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookingDto), result);
    }

    @Test
    @SneakyThrows
    void findAllBookingsByUserTest() {
        when(bookingService.findAllBookingsByUser(user.getId(), "ALL")).thenReturn(List.of(bookingDto));

        String result = mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @Test
    @SneakyThrows
    void findAllBookingsByOwnerTest() {
        when(bookingService.findAllBookingsByOwner(user.getId(), "PAST")).thenReturn(List.of(bookingDto));

        String result = mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId())
                        .param("state", "PAST"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(bookingDto)), result);
    }
}