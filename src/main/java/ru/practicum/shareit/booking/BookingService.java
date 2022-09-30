package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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
import java.util.Objects;

import static ru.practicum.shareit.booking.BookingMapper.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingService {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "start");
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public BookingDtoOutput add(long bookerId, BookingDtoInput bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found " + bookingDto.getItemId()));
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("User not found " + bookerId));
        Booking booking = toBooking(bookingDto, item, booker, State.WAITING);
        validationBooking(booking);
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Added booking id: {}", booking.getId());
        return toBookingDtoOutput(saveBooking);
    }

    @Transactional
    public BookingDtoOutput approveBooking(long bookingId, Boolean approved, long userId) {
        Booking newBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found " + bookingId));
        checkUserOfBooking(newBooking, userId);
        switch (String.valueOf(approved)) {
            case "true":
                setStateApprove(newBooking, userId);
                break;
            case "false":
                setStateRejectedOrCanceled(newBooking, userId);
                break;
        }
        log.info("Booking state: {}", newBooking.getState());
        return toBookingDtoOutput(newBooking);
    }

    private void checkState(Booking newBooking) {
        if (newBooking.getState().equals(State.APPROVED)) {
            throw new ValidationException("Booking already agreed");
        }
    }

    private void checkUserOfBooking(Booking newBooking, long userId) {
        if (userId != newBooking.getItem().getOwner().getId() &&
                userId != newBooking.getBooker().getId()) {
            throw new BookingException("The user is not associated with the booking: " + userId);

        }
    }

    private void setStateApprove(Booking newBooking, long userId) {
        if (userId == newBooking.getItem().getOwner().getId()) {
            checkState(newBooking);
            newBooking.setState(State.APPROVED);
        } else {
            throw new BookingException("Booker can not approve Booking, bookerId: " + newBooking.getBooker().getId());
        }
    }

    private void setStateRejectedOrCanceled(Booking newBooking, long userId) {
        if (userId == newBooking.getItem().getOwner().getId()) {
            newBooking.getItem().setAvailable(true);
            newBooking.setState(State.REJECTED);
        } else {
            newBooking.getItem().setAvailable(true);
            newBooking.setState(State.CANCELED);
        }
    }

    public BookingDtoOutput getById(long bookingId, long userId) {
        Booking newBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found " + bookingId));
        checkUserOfBooking(newBooking, userId);
        log.info("Get booking id: {}", newBooking.getId());
        return toBookingDtoOutput(newBooking);
    }

    public Collection<BookingDtoOutput> getBookingsOfUser(Long userId, String status, int from, int size) {
        checkUserExist(userId);
        State state;
        Page<Booking> bookings;
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, DEFAULT_SORT);
        switch (status) {
            case "ALL":
                bookings = bookingRepository.findByBookerId(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(pageable, userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(pageable, userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(pageable, userId, LocalDateTime.now());
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.findByBookerIdAndStateOrderByStartDesc(pageable, userId, state);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.findByBookerIdAndStateOrderByStartDesc(pageable, userId, state);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return toBookingsDtoOutput(bookings);
    }

    public Collection<BookingDtoOutput> getBookingsForAllItemsOfUser(long userId, String status, int from, int size) {
        checkUserExist(userId);
        State state;
        Page<Booking> bookings;
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, DEFAULT_SORT);
        switch (status) {
            case "ALL":
                bookings = bookingRepository.getBookingsOfOwner(userId, pageable);
                break;
            case "CURRENT":
                bookings = bookingRepository.getBookingsOfOwnerInCurrent(userId, LocalDateTime.now(), pageable);
                break;
            case "PAST":
                bookings = bookingRepository.getBookingsOfOwnerInPast(userId, LocalDateTime.now(), pageable);
                break;
            case "FUTURE":
                bookings = bookingRepository.getBookingsOfOwnerInFuture(userId, LocalDateTime.now(), pageable);
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.getBookingsOfOwnerAndState(userId, state, pageable);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.getBookingsOfOwnerAndState(userId, state, pageable);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return toBookingsDtoOutput(bookings);
    }

    private void validationBooking(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item not available " + booking.getItem().getAvailable());
        }
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Start: " + booking.getStart() + " or end: " + booking.getEnd() + " incorrect");
        }
        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Booker cannot be the owner");
        }
    }

    private void checkUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
    }
}
