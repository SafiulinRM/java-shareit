package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.requests.Request;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final EntityManager em;
    private final ItemRequestService service;
    private final UserService userService;

    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @InjectMocks
    ItemRequestService itemRequestService;

    @Test
    void saveRequest() {
        ItemRequestDto request = new ItemRequestDto(1L,
                "description",
                null,
                LocalDateTime.now(),
                null);
        User user = new User(1L, "pit", "some@email.com");
        userService.add(user);
        TypedQuery<User> queryUser = em.createQuery("Select u from User u where u.email = :email", User.class);
        User newUser = queryUser.setParameter("email", user.getEmail()).getSingleResult();
        service.add(1L, request);

        TypedQuery<Request> query = em.createQuery("Select r from Request r where r.description = :description", Request.class);
        Request request1 = query.setParameter("description", request.getDescription()).getSingleResult();
        assertThat(request1.getId(), notNullValue());
        assertThat(request1.getDescription(), equalTo(request.getDescription()));
    }

    @BeforeEach
    void beforeEach() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(null);
    }

    @Test
    void add() {
        User requester = new User(
                1L,
                "user1",
                "user1@mail.com");
        ItemRequestDto testItemRequestDto =
                new ItemRequestDto(1L, "description", 1L, null, null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(itemRequestRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ItemRequestDto itemRequestDto = itemRequestService.add(1L, testItemRequestDto);
        assertEquals(testItemRequestDto, itemRequestDto, "Запрос не создался");

    }

    @Test
    void getById() {
        User requester = new User(
                1L,
                "user1",
                "user1@mail.com");
        ItemRequestDto testItemRequestDto =
                new ItemRequestDto(1L, "description", 1L, null, null);
        Request testRequest = new Request(1L, "description", requester);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(testRequest));
        ItemRequestDto itemRequestDto = itemRequestService.getById(1L, 1L);
        assertEquals(testItemRequestDto, itemRequestDto, "Запрос не получен");
    }

    @Test
    void getRequestsOfUser() {
        User requester = new User(
                1L,
                "user1",
                "user1@mail.com");
        ItemRequestDto testItemRequestDto =
                new ItemRequestDto(1L, "description", 1L, null, null);
        Request testRequest = new Request(1L, "description", requester);
        when(itemRequestRepository.findByRequesterId(anyLong())).thenReturn(List.of(testRequest));
        Collection<ItemRequestDto> requests = itemRequestService.getRequestsOfUser(1L);
        assertEquals(List.of(testItemRequestDto), requests, "Запросы не получены");
    }

    @Test
    void getAllRequests() {
        User requester = new User(
                1L,
                "user1",
                "user1@mail.com");
        ItemRequestDto testItemRequestDto =
                new ItemRequestDto(1L, "description", 1L, null, null);
        Request testRequest = new Request(1L, "description", requester);
        when(itemRequestRepository.findAllByRequesterIdNot(any(), anyLong()))
                .thenReturn(new PageImpl<>(List.of(testRequest)));
        Collection<ItemRequestDto> requests = itemRequestService.getAllRequests(0, 20, 2L);
        assertEquals(List.of(testItemRequestDto), requests, "Запросы не получены");
    }
}
