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
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStateException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void addAndGetExceptionForItemNotAvailable() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", false,
                testUser, null, null, null, null);
        User booker = new User(
                2L,
                "user2",
                "user2@mail.com");
        BookingDtoInput testBookingDtoInput = new BookingDtoInput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), 1L, 2L, State.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertThrows(ValidationException.class,
                () -> bookingService.add(2L, testBookingDtoInput), "Ошибка исключения");
    }

    @Test
    void addAndGetExceptionForBookingStartAfterEnd() {
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
        BookingDtoInput testBookingDtoInput = new BookingDtoInput(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.now(), 1L, 2L, State.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertThrows(ValidationException.class,
                () -> bookingService.add(2L, testBookingDtoInput), "Ошибка исключения");
    }

    @Test
    void addAndGetExceptionForBookerIsOwner() {
        User bookerAndOwner = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                bookerAndOwner, null, null, null, null);

        BookingDtoInput testBookingDtoInput = new BookingDtoInput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), 1L, 1L, State.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(bookerAndOwner));
        when(bookingRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        assertThrows(NotFoundException.class,
                () -> bookingService.add(1L, testBookingDtoInput), "Ошибка исключения");
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
    void approveBookingAlreadyApprovedFail() {
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
                LocalDateTime.now(), testItem, booker, State.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        assertThrows(ValidationException.class,
                () -> bookingService.approveBooking(1L, true, 1L), "Ошибка исключения");
    }

    @Test
    void approveBookingWithOtherUserFail() {
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
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(testBooking));
        assertThrows(BookingException.class,
                () -> bookingService.approveBooking(1L, true, 3L), "Ошибка исключения");
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
    void getBookingsOfUserStateAllAndCurrent() {
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
    void getBookingsOfUserStatePastAndFuture() {
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
        Booking testBookingPast = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.APPROVED);
        BookingDtoOutput testBookingDtoOutPutPast = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.APPROVED);
        Booking testBookingFuture = new Booking(2L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.APPROVED);
        BookingDtoOutput testBookingDtoOutPutFuture = new BookingDtoOutput(2L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.APPROVED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(any(), anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingPast)));
        when(bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(any(), anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingFuture)));
        Collection<BookingDtoOutput> bookingsPast =
                bookingService.getBookingsOfUser(1L, "PAST", 0, 20);
        assertEquals(List.of(testBookingDtoOutPutPast), bookingsPast, "Бронирования получены");
        Collection<BookingDtoOutput> bookingsFuture =
                bookingService.getBookingsOfUser(1L, "FUTURE", 0, 20);
        assertEquals(List.of(testBookingDtoOutPutFuture), bookingsFuture, "Бронирования получены");
    }

    @Test
    void getBookingsOfUserStateWaitingAndRejected() {
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
        Booking testBookingWaiting = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutPutWaiting = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        Booking testBookingRejected = new Booking(2L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.REJECTED);
        BookingDtoOutput testBookingDtoOutPutRejected = new BookingDtoOutput(2L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.REJECTED);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndStateOrderByStartDesc(any(), anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingWaiting)));
        Collection<BookingDtoOutput> bookingsWaiting =
                bookingService.getBookingsOfUser(1L, "WAITING", 0, 20);
        assertEquals(List.of(testBookingDtoOutPutWaiting), bookingsWaiting, "Бронирования получены");
        when(bookingRepository.findByBookerIdAndStateOrderByStartDesc(any(), anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingRejected)));
        Collection<BookingDtoOutput> bookingsRejected =
                bookingService.getBookingsOfUser(1L, "REJECTED", 0, 20);
        assertEquals(List.of(testBookingDtoOutPutRejected), bookingsRejected, "Бронирования получены");
    }

    @Test
    void getBookingsOfUserNotExistFail() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingsOfUser(1L, "ALL", 0, 20));
    }

    @Test
    void getBookingsOfUserWrongStateFail() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        assertThrows(UnsupportedStateException.class,
                () -> bookingService.getBookingsOfUser(1L, "qwerty", 0, 20));
    }

    @Test
    void getBookingsForAllItemsOfUserStateAllAndCurrent() {
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

    @Test
    void getBookingsForAllItemsOfUserStatePastAndFuture() {
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
        Booking testBookingPast = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutputPast = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingsOfOwnerInPast(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingPast)));
        Collection<BookingDtoOutput> bookingsDtoOutputPast =
                bookingService.getBookingsForAllItemsOfUser(1L, "PAST", 0, 20);
        assertEquals(List.of(testBookingDtoOutputPast), bookingsDtoOutputPast, "Бронирования не получили");
        Booking testBookingFuture = new Booking(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutputFuture = new BookingDtoOutput(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        when(bookingRepository.getBookingsOfOwnerInFuture(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingFuture)));
        Collection<BookingDtoOutput> bookingsDtoOutputFuture =
                bookingService.getBookingsForAllItemsOfUser(1L, "FUTURE", 0, 20);
        assertEquals(List.of(testBookingDtoOutputFuture), bookingsDtoOutputFuture, "Бронирования не получили");
    }

    @Test
    void getBookingsForAllItemsOfUserStateWaitingAndRejected() {
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
        Booking testBookingWaiting = new Booking(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        BookingDtoOutput testBookingDtoOutputWaiting = new BookingDtoOutput(1L,
                LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.of(2, 2, 3, 4, 5, 6),
                testItem, booker, State.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.getBookingsOfOwnerAndState(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingWaiting)));
        Collection<BookingDtoOutput> bookingsDtoOutputWaiting =
                bookingService.getBookingsForAllItemsOfUser(1L, "WAITING", 0, 20);
        assertEquals(List.of(testBookingDtoOutputWaiting), bookingsDtoOutputWaiting, "Бронирования не получили");
        Booking testBookingRejected = new Booking(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.REJECTED);
        BookingDtoOutput testBookingDtoOutputRejected = new BookingDtoOutput(1L,
                LocalDateTime.of(2023, 2, 3, 4, 5, 6),
                LocalDateTime.of(2024, 2, 3, 4, 5, 6),
                testItem, booker, State.REJECTED);
        when(bookingRepository.getBookingsOfOwnerAndState(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(testBookingRejected)));
        Collection<BookingDtoOutput> bookingsDtoOutputRejected =
                bookingService.getBookingsForAllItemsOfUser(1L, "REJECTED", 0, 20);
        assertEquals(List.of(testBookingDtoOutputRejected), bookingsDtoOutputRejected, "Бронирования не получили");
    }
}