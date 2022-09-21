package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
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

import static ru.practicum.shareit.booking.BookingMapper.toBooking;

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
    public Booking add(long bookerId, BookingDtoInput bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found " + bookingDto.getItemId()));
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Item not found " + bookerId));
        Booking booking = toBooking(bookingDto, item, booker, State.WAITING);
        validationBooking(booking);
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Added booking id: {}", booking.getId());
        return saveBooking;
    }

    @Transactional
    public Booking approveBooking(long bookingId, Boolean approved, long userId) {
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
        return newBooking;
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

    public Booking getById(long bookingId, long userId) {
        Booking newBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found " + bookingId));
        checkUserOfBooking(newBooking, userId);
        log.info("Get booking id: {}", newBooking.getId());
        return newBooking;
    }

    public Collection<Booking> getBookingsOfUser(long userId, String status) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        State state;
        Collection<Booking> bookings;
        switch (status) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.findByBookerIdAndStateOrderByStartDesc(userId, state);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.findByBookerIdAndStateOrderByStartDesc(userId, state);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    public Collection<Booking> getBookingsForAllItemsOfUser(long userId, String status) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        State state;
        Collection<Booking> bookings;
        switch (status) {
            case "ALL":
                bookings = bookingRepository.getBookingsOfOwner(userId, DEFAULT_SORT);
                break;
            case "CURRENT":
                bookings = bookingRepository.getBookingsOfOwnerInCurrent(userId, LocalDateTime.now(), DEFAULT_SORT);
                break;
            case "PAST":
                bookings = bookingRepository.getBookingsOfOwnerInPast(userId, LocalDateTime.now(), DEFAULT_SORT);
                break;
            case "FUTURE":
                bookings = bookingRepository.getBookingsOfOwnerInFuture(userId, LocalDateTime.now(), DEFAULT_SORT);
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.getBookingsOfOwnerAndState(userId, state, DEFAULT_SORT);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.getBookingsOfOwnerAndState(userId, state, DEFAULT_SORT);
                break;
            default:
                throw new UnsupportedStateException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private void validationBooking(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item not available " + booking.getItem().getAvailable());
        }
        if (!booking.getStart().isBefore(booking.getEnd())) {
            throw new ValidationException("Start: " + booking.getStart() + " or end: " + booking.getEnd() + " incorrect");
        }
        if (booking.getBooker().getId() == booking.getItem().getOwner().getId()) {
            throw new NotFoundException("Booker cannot be the owner");
        }
    }
}
