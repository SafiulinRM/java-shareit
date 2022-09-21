package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toBooking(BookingDtoInput bookingDto, Item item, User booker, State state) {
        return new Booking(bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                state);
    }

    public static BookingDtoOutput toBookingDtoOutput(Booking booking) {
        return new BookingDtoOutput(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getState());
    }

    public static Collection<BookingDtoOutput> toBookingsDtoOutput(Collection<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDtoOutput)
                .collect(toList());
    }
}
