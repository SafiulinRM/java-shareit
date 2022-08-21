package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * // TODO .
 */

@Data
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    private String email;
}
