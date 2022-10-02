package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Column(name = "description", nullable = false, length = 4000)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "request_id")
    private Long requestId;
    @Transient
    private BookingShort lastBooking;
    @Transient
    private BookingShort nextBooking;
    @Transient
    private Collection<CommentDto> comments;
}