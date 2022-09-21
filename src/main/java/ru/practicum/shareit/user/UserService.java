package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.Collection;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User add(User user) {
        User newUser = userRepository.save(user);
        log.info("Added user name: {}", user.getName());
        return newUser;
    }

    public User getById(long id) {
        log.info("Got user id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found " + id));
    }

    @Transactional
    public User update(User user) {
        User newUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("User not found " + user.getId()));
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }
        log.info("Updated user id: {}", user.getId());
        return newUser;
    }

    public Collection<User> getAll() {
        Collection<User> usersDto = userRepository.findAll();
        log.info("Got users: {}", usersDto.size());
        return usersDto;
    }

    @Transactional
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }
}