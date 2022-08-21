package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import static ru.practicum.shareit.IdGenerator.generateUserId;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User toUser(UserDto userDto) {
        return new User(
                generateUserId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }
}