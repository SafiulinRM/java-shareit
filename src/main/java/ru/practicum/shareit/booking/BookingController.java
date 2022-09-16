package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDtoOutput;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_STATE = "ALL";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput add(@RequestHeader(USER_ID_HEADER) long userId, @RequestBody BookingDtoInput bookingDto) {
        return toBookingDtoOutput(bookingService.add(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput approveBooking(@PathVariable long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) long userId) {
        return toBookingDtoOutput(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        return toBookingDtoOutput(bookingService.getById(bookingId, userId));
    }

    @GetMapping()
    public Collection<BookingDtoOutput> getBookingsOfUser
            (@RequestHeader(USER_ID_HEADER) long userId,
             @RequestParam(required = false, defaultValue = DEFAULT_STATE) String state) {
        return bookingService.getBookingsOfUser(userId, state).stream()
                .map(BookingMapper::toBookingDtoOutput)
                .collect(toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getBookingsForAllItemsOfUser
            (@RequestHeader(USER_ID_HEADER) long userId,
             @RequestParam(required = false, defaultValue = DEFAULT_STATE) String state) {
        return bookingService.getBookingsForAllItemsOfUser(userId, state).stream()
                .map(BookingMapper::toBookingDtoOutput)
                .collect(toList());
    }
}
