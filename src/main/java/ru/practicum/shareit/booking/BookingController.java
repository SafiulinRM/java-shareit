package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDtoInfo;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    public static final String HEADER_OF_OWNER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoInfo add(@RequestHeader(HEADER_OF_OWNER) long userId, @RequestBody BookingDto bookingDto) {
        return toBookingDtoInfo(bookingService.add(userId, bookingDto));
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoInfo approveBooking(@PathVariable long bookingId,
                                         @RequestParam Boolean approved, @RequestHeader(HEADER_OF_OWNER) long userId) {
        return toBookingDtoInfo(bookingService.approveBooking(bookingId, approved, userId));
    }

    @GetMapping("/{bookingId}")
    public BookingDtoInfo getById(@PathVariable long bookingId, @RequestHeader(HEADER_OF_OWNER) long userId) {
        return toBookingDtoInfo(bookingService.getById(bookingId, userId));
    }

    @GetMapping()
    public Collection<BookingDtoInfo> getBookingsOfUser(@RequestHeader(HEADER_OF_OWNER) long userId,
                                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfUser(userId, state)
                .stream().map(BookingMapper::toBookingDtoInfo).collect(toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDtoInfo> getBookingsForAllItemsOfUser(@RequestHeader(HEADER_OF_OWNER) long userId,
                                                                   @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getBookingsForAllItemsOfUser(userId, state)
                .stream().map(BookingMapper::toBookingDtoInfo).collect(toList());
    }
}
