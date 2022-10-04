package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBookerId(Long userId, Pageable pageable);

    Page<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime start, LocalDateTime end);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Pageable pageable, long userId, LocalDateTime end);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Pageable pageable, long userId, LocalDateTime start);

    Page<Booking> findByBookerIdAndStateOrderByStartDesc(Pageable pageable, long userId, State state);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.owner.id = ?1)")
    Page<Booking> getBookingsOfOwner(long userId, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.owner.id = ?1) AND ?2 between b.start and b.end")
    Page<Booking> getBookingsOfOwnerInCurrent(long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.owner.id = ?1) AND b.end <=?2")
    Page<Booking> getBookingsOfOwnerInPast(long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.owner.id = ?1) AND b.start > ?2")
    Page<Booking> getBookingsOfOwnerInFuture(long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item in (select i from Item i where i.owner.id = ?1) AND b.state = ?2")
    Page<Booking> getBookingsOfOwnerAndState(long userId, State state, Pageable pageable);

    BookingShort findByItemIdAndStartBefore(long itemId, LocalDateTime now);

    BookingShort findByItemIdAndStartAfter(long itemId, LocalDateTime now);

    List<Booking> findByBookerId(long bookerId);
}
