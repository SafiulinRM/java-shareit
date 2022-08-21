package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserStorage {
    UserDto getUserById(long id);

    Collection<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    void deleteUserById(long id);

    boolean isExistById(long id);
}