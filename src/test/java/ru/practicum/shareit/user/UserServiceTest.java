package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserService userService;
    private final EntityManager em;
    private final UserService service;

    @Test
    void saveUser() {
        User user = new User(1L, "pit", "some@email.com");
        service.add(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = query.setParameter("email", user.getEmail()).getSingleResult();
        assertThat(newUser.getId(), notNullValue());
        assertThat(newUser.getName(), equalTo(user.getName()));
        assertThat(newUser.getEmail(), equalTo(user.getEmail()));


    }

    @Test
    void add() {
        User testUser = new User(
                null,
                "user1",
                "user1@mail.com");
        Mockito.when(userRepository.save(any())).thenReturn(testUser);
        User user = userService.add(testUser);
        assertEquals(testUser, user, "Юзер не создался!");
    }

    @Test
    void getById() {
        User testUser = new User(
                null,
                "user1",
                "user1@mail.com");
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        User user = userService.getById(1);
        assertEquals(testUser, user, "Юзер не получили!");
    }

    @Test
    void update() {
        User testUser = new User(
                null,
                "user1",
                "user1@mail.com");
        User user1update = new User(
                1L,
                "user1update",
                "user1update@mail.com");
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        User user = userService.update(user1update);
        assertEquals(testUser, user, "Юзер не обновили!");
    }

    @Test
    void getAll() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser));
        Collection<User> users = userService.getAll();

        assertEquals(List.of(testUser), users, "Юзеров не получили!");
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(1L);
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);
    }
}
