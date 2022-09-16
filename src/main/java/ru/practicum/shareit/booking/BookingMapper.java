package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

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
}
