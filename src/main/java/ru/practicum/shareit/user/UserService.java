package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public UserDto add(UserDto userDto) {
        UserDto newUserDto = userStorage.addUser(userDto);
        log.info("Added user name: {}", userDto.getName());
        return newUserDto;
    }

    public UserDto getById(long id) {
        checkUserExist(id);
        log.info("Got user id: {}", id);
        return userStorage.getUserById(id);
    }

    public UserDto update(UserDto userDto, long userId) {
        checkUserExist(userId);
        userDto.setId(userId);
        UserDto newUserDto = userStorage.updateUser(userDto);
        log.info("Updated user id: {}", userDto.getId());
        return newUserDto;
    }

    public Collection<UserDto> getAll() {
        Collection<UserDto> usersDto = userStorage.getAllUsers();
        log.info("Got users: {}", usersDto.size());
        return usersDto;
    }

    public void deleteUserById(long userId) {
        checkUserExist(userId);
        userStorage.deleteUserById(userId);
    }

    private void checkUserExist(long id) {
        if (!userStorage.isExistById(id)) {
            log.warn("User with id: {} doesn't exist!", id);
            throw new NotFoundException(String.format("User with id: %d doesn't exist!", id));
        }
    }
}