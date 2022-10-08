package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    public static final String DEFAULT_STATE = "ALL";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput add(@RequestHeader(USER_ID_HEADER) long userId,
                                @RequestBody BookingDtoInput bookingDto) {
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOutput approveBooking(@PathVariable long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.approveBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOutput getById(@PathVariable long bookingId, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping()
    public Collection<BookingDtoOutput> getBookingsOfUser(@RequestHeader(USER_ID_HEADER) long userId,
                                                          @RequestParam(required = false, defaultValue = DEFAULT_STATE) String state,
                                                          @RequestParam(required = false, defaultValue = "0") int from,
                                                          @RequestParam(required = false, defaultValue = "10") int size) {
        return bookingService.getBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getBookingsForAllItemsOfUser(@RequestHeader(USER_ID_HEADER) long userId,
                                                                     @RequestParam(required = false, defaultValue = DEFAULT_STATE) String state,
                                                                     @RequestParam(required = false, defaultValue = "0") int from,
                                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        return bookingService.getBookingsForAllItemsOfUser(userId, state, from, size);
    }
}
