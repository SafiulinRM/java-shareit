package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User add(User user) {
        User newUser = userRepository.save(user);
        log.info("Added user name: {}", user.getName());
        return newUser;
    }

    public User getById(long id) {
        log.info("Got user id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found " + id));
    }

    public User update(User user) {
        User newUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found " + user.getId()));
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        userRepository.save(newUser);
        log.info("Updated user id: {}", user.getId());
        return newUser;
    }

    public Collection<User> getAll() {
        Collection<User> usersDto = userRepository.findAll();
        log.info("Got users: {}", usersDto.size());
        return usersDto;
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}