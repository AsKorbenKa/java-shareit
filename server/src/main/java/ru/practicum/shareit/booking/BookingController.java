package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewRequestBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody NewRequestBookingDto bookingDto) {
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable("bookingId") Long bookingId,
                              @RequestParam(name = "approved") boolean approved) {
        return bookingService.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public Collection<BookingDto> findAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(name = "state", defaultValue = "ALL") String bookingState) {
        return bookingService.findAllBookingsByUser(userId, bookingState);
    }

    @GetMapping("/owner")
    public Collection<BookingDto> findAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String bookingState) {
        return bookingService.findAllBookingsByOwner(userId, bookingState);
    }
}
