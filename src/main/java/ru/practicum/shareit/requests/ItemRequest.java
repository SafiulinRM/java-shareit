package ru.practicum.shareit.requests;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * // TODO .
 */

@Data
public class ItemRequest {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;
}
