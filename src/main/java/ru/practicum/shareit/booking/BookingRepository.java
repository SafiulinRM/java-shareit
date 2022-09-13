package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdOrderByStartDesc(long userId);

    List<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(long userId, State status);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.ownerId = ?1) " +
            "order by b.start desc")
    List<Booking> getBookingsOfOwner(long userId);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.ownerId = ?1) AND ?2 between b.start and b.end " +
            "order by b.start desc")
    List<Booking> getBookingsOfOwnerInCurrent(long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.ownerId = ?1) AND b.end <=?2 " +
            "order by b.start desc")
    List<Booking> getBookingsOfOwnerInPast(long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.ownerId = ?1) AND b.start > ?2 " +
            "order by b.start desc")
    List<Booking> getBookingsOfOwnerInFuture(long userId, LocalDateTime now);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.ownerId = ?1) AND b.status = ?2 " +
            "order by b.start desc")
    List<Booking> getBookingsOfOwnerAndStatus(long userId, State status);

    BookingShort findByItem_IdAndStartBefore(long itemId, LocalDateTime now);

    BookingShort findByItem_IdAndStartAfter(long itemId, LocalDateTime now);

    List<Booking> findByBooker_Id(long bookerId);
}
