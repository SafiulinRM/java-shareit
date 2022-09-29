package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @InjectMocks
    BookingService bookingService;


    @Test
    void add() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        BookingDtoInput testBookingDtoInput = new BookingDtoInput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), 1L, 2L, State.WAITING);
        BookingDtoOutput testBookingDtoOutput = new BookingDtoOutput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                testBookingDtoInput.getEnd(), testItem, booker, State.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        BookingDtoOutput bookingDtoOutput = bookingService.add(2L, testBookingDtoInput);
        assertEquals(testBookingDtoOutput, bookingDtoOutput, "Бронирование не сохранилось");
    }

    @Test
    void approveBooking() {
        User owner = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                owner, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        Booking testBooking = new Booking(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutput = new BookingDtoOutput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                testBooking.getEnd(), testItem, booker, State.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        BookingDtoOutput bookingDtoOutput = bookingService.approveBooking(1L, true, 1L);
        assertEquals(testBookingDtoOutput, bookingDtoOutput, "Статус не обновился");
        BookingDtoOutput bookingDtoOutputCanceled = bookingService.approveBooking(1L, false, 2L);
        assertEquals(bookingDtoOutputCanceled.getStatus(), State.CANCELED, "Статус не обновился");
        BookingDtoOutput bookingDtoOutputRejected = bookingService.approveBooking(1L, false, 1L);
        assertEquals(bookingDtoOutputRejected.getStatus(), State.REJECTED, "Статус не обновился");
    }

    @Test
    void getById() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        Booking testBooking = new Booking(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutput = new BookingDtoOutput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                testBooking.getEnd(), testItem, booker, State.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        BookingDtoOutput bookingDtoOutput = bookingService.getById(1L, 1L);
        assertEquals(testBookingDtoOutput, bookingDtoOutput, "Бронирование не получили");
    }

    @Test
    void getBookingsOfUser() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        Booking testBooking = new Booking(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutput = new BookingDtoOutput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                testBooking.getEnd(), testItem, booker, State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findByBookerId(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Collection<BookingDtoOutput> bookingsDtoOutput =
                bookingService.getBookingsOfUser(2L, "ALL", 0, 20);
        assertEquals(List.of(testBookingDtoOutput), bookingsDtoOutput, "Бронирования не получили");
        Booking testBookingCurrent = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutputCurrent = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(any(), anyLong(),
                any(), any())).thenReturn(new PageImpl<>(List.of(testBookingCurrent)));
        Collection<BookingDtoOutput> bookingsDtoOutputCurrent =
                bookingService.getBookingsOfUser(2L, "CURRENT", 0, 20);
        assertEquals(List.of(testBookingDtoOutputCurrent), bookingsDtoOutputCurrent, "Бронирования не получили");
    }

    @Test
    void getBookingsForAllItemsOfUser() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        Booking testBooking = new Booking(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutput = new BookingDtoOutput(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                testBooking.getEnd(), testItem, booker, State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingsOfOwner(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBooking)));
        Collection<BookingDtoOutput> bookingsDtoOutput =
                bookingService.getBookingsForAllItemsOfUser(1L, "ALL", 0, 20);
        assertEquals(List.of(testBookingDtoOutput), bookingsDtoOutput, "Бронирования не получили");
        Booking testBookingCurrent = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutputCurrent = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        when(bookingRepository.getBookingsOfOwnerInCurrent(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingCurrent)));
        Collection<BookingDtoOutput> bookingsDtoOutputCurrent =
                bookingService.getBookingsForAllItemsOfUser(1L, "CURRENT", 0, 20);
        assertEquals(List.of(testBookingDtoOutputCurrent), bookingsDtoOutputCurrent, "Бронирования не получили");
    }
}