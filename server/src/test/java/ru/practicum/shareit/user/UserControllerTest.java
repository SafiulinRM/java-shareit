package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController controller;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private User user1;
    private User user1update;
    private User user2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        user1 = new User(
                1L,
                "user1",
                "user1@mail.com");
        user1update = new User(
                1L,
                "user1update",
                "user1update@mail.com");
        user2 = new User(
                2L,
                "user2",
                "user2@mail.com");
    }

    @Test
    void saveNewUser() throws Exception {
        when(userService.add(any()))
                .thenReturn(user1);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void getUser() throws Exception {
        when(userService.getById(anyLong()))
                .thenReturn(user1);
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.email", is(user1.getEmail())));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(any()))
                .thenReturn(user1update);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(user1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(user1update.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user1update.getName())))
                .andExpect(jsonPath("$.email", is(user1update.getEmail())));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(user1, user2));
        mvc.perform(get("/users"))
                .andExpect(content().json(mapper.writeValueAsString(List.of(user1, user2))));
    }

    @Test
    void deleteUser() throws Exception {
        when(userService.deleteUserById(anyLong()))
                .thenReturn(user1);
        mvc.perform(delete("/users/1"))
                .andExpect(content().json(mapper.writeValueAsString(user1)));
        Mockito.verify(userService, Mockito.times(1))
                .deleteUserById(1);
    }
}
