package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static ru.practicum.shareit.booking.BookingMapper.toBooking;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public Booking add(long booker_Id, BookingDto bookingDto) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found " + bookingDto.getItemId()));
        User booker = userRepository.findById(booker_Id)
                .orElseThrow(() -> new NotFoundException("Item not found " + booker_Id));
        Booking booking = toBooking(bookingDto, item, booker, State.WAITING);
        validationBooking(booking);
        Booking saveBooking = bookingRepository.save(booking);
        log.info("Added booking id: {}", booking.getId());
        return saveBooking;
    }

    public Booking approveBooking(long bookingId, Boolean approved, long userId) {
        Booking newBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found " + bookingId));
        if (approved && userId == newBooking.getItem().getOwnerId()) {
            if (newBooking.getStatus().equals(State.APPROVED)) {
                throw new ValidationException("Booking already agreed");
            }
            newBooking.setStatus(State.APPROVED);
        } else if (!approved && userId == newBooking.getItem().getOwnerId()) {
            newBooking.getItem().setAvailable(true);
            newBooking.setStatus(State.REJECTED);
        } else if (!approved && userId == newBooking.getBooker().getId()) {
            newBooking.getItem().setAvailable(true);
            newBooking.setStatus(State.CANCELED);
        } else {
            throw new NotFoundException("Approve the booking failed");
        }
        bookingRepository.save(newBooking);
        log.info("Booking status: {}", newBooking.getStatus());
        return newBooking;
    }

    public Booking getById(long bookingId, long userId) {
        Booking newBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found " + bookingId));
        if (newBooking.getItem().getOwnerId() == userId || newBooking.getBooker().getId() == userId) {
            log.info("Get booking id: {}", newBooking.getId());
            return newBooking;
        } else {
            throw new NotFoundException("The user is not associated with the booking: " + userId);
        }
    }

    public Collection<Booking> getBookingsOfUser(long userId, String status) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        State state;
        Collection<Booking> bookings = new ArrayList<>();
        switch (status) {
            case "ALL":
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, state);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId, state);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    public Collection<Booking> getBookingsForAllItemsOfUser(long userId, String status) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        State state;
        Collection<Booking> bookings = new ArrayList<>();
        switch (status) {
            case "ALL":
                bookings = bookingRepository.getBookingsOfOwner(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.getBookingsOfOwnerInCurrent(userId,
                        LocalDateTime.now());
                break;
            case "PAST":
                bookings = bookingRepository.getBookingsOfOwnerInPast(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.getBookingsOfOwnerInFuture(userId, LocalDateTime.now());
                break;
            case "WAITING":
                state = State.WAITING;
                bookings = bookingRepository.getBookingsOfOwnerAndStatus(userId, state);
                break;
            case "REJECTED":
                state = State.REJECTED;
                bookings = bookingRepository.getBookingsOfOwnerAndStatus(userId, state);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings;
    }

    private void validationBooking(Booking booking) {
        if (!booking.getItem().getAvailable()) {
            throw new ValidationException("Item not available " + booking.getItem().getAvailable());
        }
        if (booking.getStart().isAfter(booking.getEnd()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getStart().equals(booking.getEnd()) ||
                booking.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start: " + booking.getStart() + " or end: " + booking.getEnd() + " incorrect");
        }
        if (booking.getBooker().getId() == booking.getItem().getOwnerId()) {
            throw new NotFoundException("Booker cannot be the owner");
        }
    }
}
