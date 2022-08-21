package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AddressException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    @Getter
    private static final Map<Long, User> users = new HashMap<>();

    @Override
    public UserDto getUserById(long id) {
        return toUserDto(users.get(id));
    }

    @Override
    public Collection<UserDto> getAllUsers() {
        Collection<UserDto> usersDto = new ArrayList<>();
        for (User user : users.values()) {
            usersDto.add(toUserDto(user));
        }
        return usersDto;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        checkUserEmailUnique(userDto.getEmail());
        User user = toUser(userDto);
        users.put(user.getId(), user);
        return getUserById(user.getId());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = users.get(userDto.getId());
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            checkUserEmailUnique(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
        return getUserById(user.getId());
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
    }

    @Override
    public boolean isExistById(long id) {
        return users.containsKey(id);
    }

    private void checkUserEmailUnique(String email) {
        boolean emailSame = false;
        for (User u : users.values()) {
            if (email.equals(u.getEmail())) {
                emailSame = true;
            }
        }
        if (emailSame) {
            log.warn("Email duplicate {}", email);
            throw new AddressException("Email duplicate " + email);
        }
    }
}