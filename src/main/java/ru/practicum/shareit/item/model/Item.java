package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.CommentDto;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    @Column(name = "is_available")
    private Boolean available;
    @Column(name = "owner_id")
    private long ownerId;
    @Column(name = "request_id")
    private Long requestId;
    @Transient
    private BookingShort lastBooking;
    @Transient
    private BookingShort nextBooking;
    @Transient
    private Collection<CommentDto> comments;
}