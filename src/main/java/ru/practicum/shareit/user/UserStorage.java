package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserStorage {
    User getUserById(long id);

    Collection<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    void deleteUserById(long id);
}