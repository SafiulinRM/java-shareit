package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User add(User user) {
        User newUser = userStorage.addUser(user);
        log.info("Added user name: {}", user.getName());
        return newUser;
    }

    public User getById(long id) {
        log.info("Got user id: {}", id);
        return userStorage.getUserById(id);
    }

    public User update(User user) {
        User newUser = userStorage.updateUser(user);
        log.info("Updated user id: {}", user.getId());
        return newUser;
    }

    public Collection<User> getAll() {
        Collection<User> usersDto = userStorage.getAllUsers();
        log.info("Got users: {}", usersDto.size());
        return usersDto;
    }

    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
    }
}