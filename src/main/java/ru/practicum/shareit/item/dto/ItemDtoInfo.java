package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShort;

import java.util.Collection;

@Data
@AllArgsConstructor
public class ItemDtoInfo {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingShort lastBooking;
    private BookingShort nextBooking;
    private Collection<CommentDto> comments;
}
