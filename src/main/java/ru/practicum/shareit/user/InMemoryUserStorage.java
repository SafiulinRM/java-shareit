package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UserAlreadyExistsException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.practicum.shareit.IdGenerator.generateUserId;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();

    public static Map<Long, User> getUsers() {
        return new HashMap(users);
    }

    @Override
    public User getUserById(long id) {
        checkExist(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        checkUserEmailUnique(user.getEmail());
        user.setId(generateUserId());
        users.put(user.getId(), user);
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        User updateUser = users.get(user.getId());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkUserEmailUnique(user.getEmail());
            updateUser.setEmail(user.getEmail());
        }
        return getUserById(user.getId());
    }

    @Override
    public void deleteUserById(long id) {
        checkExist(id);
        users.remove(id);
    }

    private void checkExist(long id) {
        if (!users.containsKey(id)) {
            log.warn("User with id: {} doesn't exist!", id);
            throw new NotFoundException(String.format("User with id: %d doesn't exist!", id));
        }
    }

    private void checkUserEmailUnique(String email) {
        boolean userAlreadyExists = users.values().stream().anyMatch(user -> email.equals(user.getEmail()));
        if (userAlreadyExists) {
            log.warn("Email duplicate {}", email);
            throw new UserAlreadyExistsException("Email duplicate " + email);
        }
    }
}