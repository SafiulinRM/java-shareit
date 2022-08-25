package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.user.UserMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return toUserDto(userService.getById(userId));
    }

    @PostMapping
    public UserDto add(@Validated({Create.class}) @RequestBody UserDto userDto) {
        return toUserDto(userService.add(toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Validated({Update.class}) @RequestBody UserDto userDto, @PathVariable long userId) {
        return toUserDto(userService.update(updateUser(userDto, userId)));
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.getAll().stream().map(user -> toUserDto(user)).collect(toList());
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userService.deleteUserById(userId);
    }
}