package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
public class Item {
    private final long id;
    private String name;
    private String description;
    private boolean available;
    private final long owner;
    private ItemRequest request;
}