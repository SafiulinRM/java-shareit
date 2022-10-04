package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    BookingRepository bookingRepository;
    User user;
    User booker;
    Item item;
    Booking booking;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "pit", "some@email.com"));
        booker = userRepository.save(new User(2L, "user2", "user2@mail.com"));
        item = itemRepository.save(new Item(1L, "item", "description", true,
                user, null, null, null, null));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void getBookingsOfOwner() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                item, booker, State.APPROVED));
        final Page<Booking> bookings = bookingRepository.getBookingsOfOwner(user.getId(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void getBookingsOfOwnerInCurrent() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                item, booker, State.WAITING));
        final Page<Booking> bookings = bookingRepository
                .getBookingsOfOwnerInCurrent(user.getId(), LocalDateTime.now(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void getBookingsOfOwnerInPast() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                item, booker, State.APPROVED));
        final Page<Booking> bookings = bookingRepository
                .getBookingsOfOwnerInPast(user.getId(), LocalDateTime.now(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void getBookingsOfOwnerInFuture() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                item, booker, State.WAITING));
        final Page<Booking> bookings = bookingRepository
                .getBookingsOfOwnerInFuture(user.getId(), LocalDateTime.now(), Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.getTotalElements());
    }

    @Test
    void getBookingsOfOwnerAndState() {
        booking = bookingRepository.save(new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                item, booker, State.APPROVED));
        final Page<Booking> bookings = bookingRepository
                .getBookingsOfOwnerAndState(user.getId(), State.APPROVED, Pageable.unpaged());
        assertNotNull(bookings);
        assertEquals(1, bookings.getTotalElements());
    }
}