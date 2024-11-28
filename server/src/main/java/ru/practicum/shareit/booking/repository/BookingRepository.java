package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatuses;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerId(Long userId);

    Collection<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatuses status);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllUserBookingsInPast(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Collection<Booking> findAllUserBookingsInPresent(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.booker.id = ?1 " +
            "and CURRENT_TIMESTAMP < b.start")
    Collection<Booking> findAllUserBookingsInFuture(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1")
    Collection<Booking> findAllOwnerBookings(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1" +
            "and CURRENT_TIMESTAMP > b.end")
    Collection<Booking> findAllOwnerBookingsInPast(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1" +
            "and CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Collection<Booking> findAllOwnerBookingsInPresent(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1" +
            "and CURRENT_TIMESTAMP BETWEEN b.start AND b.end")
    Collection<Booking> findAllOwnerBookingsInFuture(Long userId);

    @Query("select b " +
            "from Booking as b " +
            "where b.item.user.id = ?1" +
            "and b.status = ?2")
    Collection<Booking> findAllOwnerBookingsByIdAndStatus(Long userId, BookingStatuses status);

    @Query("select b.start " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and ?2 < b.start")
    Collection<LocalDateTime> findNextBookingStartByItemId(Long itemId, LocalDateTime dateTime);

    @Query("select b.end " +
            "from Booking as b " +
            "where b.item.id = ?1 " +
            "and ?2 > b.end")
    Collection<LocalDateTime> findLastBookingEndByItemId(Long itemId, LocalDateTime dateTime);

    Boolean existsByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime localDateTime);
}
