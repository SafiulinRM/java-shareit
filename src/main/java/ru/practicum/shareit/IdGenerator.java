package ru.practicum.shareit;

public class IdGenerator {
    private static long userId = 0;
    private static long itemId = 0;
    private static long itemRequestId = 0;
    private static long bookingId = 0;

    public static long generateUserId() {
        return ++userId;
    }

    public static long generateItemId() {
        return ++itemId;
    }

    public static long generateItemRequestId() {
        return ++itemRequestId;
    }

    public static long generateBookingId() {
        return ++bookingId;
    }
}