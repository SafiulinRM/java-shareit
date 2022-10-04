package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnlarged;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemService itemService;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(itemRepository,
                userRepository,
                bookingRepository,
                commentRepository);
    }

    @Test
    void add() {
        ItemDto testItemDto = new ItemDto(1L, "item", "description", true,
                null);
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        when(itemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        ItemDto itemDto = itemService.add(testItemDto, 1L);
        assertEquals(itemDto, testItemDto, "Вещь не добавили");
    }

    @Test
    void update() {
        ItemDto updateItemDto = new ItemDto(1L, "updateName", "updateDescription", true,
                null);
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        ItemDto itemDto = itemService.update(updateItemDto, 1L, 1L);
        assertEquals(itemDto, updateItemDto, "Вещь не обновили");
    }

    @Test
    void getByItemId() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        User author = new User(
                2L,
                "user2",
                "user2@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        BookingShort lastBooking = new BookingShort() {
            @Override
            public Long getId() {
                return 1L;
            }

            @Override
            public Long getBookerId() {
                return 2L;
            }
        };
        BookingShort nextBooking = new BookingShort() {
            @Override
            public Long getId() {
                return 2L;
            }

            @Override
            public Long getBookerId() {
                return 3L;
            }
        };
        Comment comment = new Comment(1L, "description", testItem, author);
        CommentDto commentDto = new CommentDto(1L, "description", author.getName(), null);
        ItemDtoEnlarged testItemDtoEnlarged = new ItemDtoEnlarged(1L, "item", "description", true,
                null, lastBooking, nextBooking, List.of(commentDto));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(bookingRepository.findByItemIdAndStartBefore(anyLong(), any())).thenReturn(lastBooking);
        when(bookingRepository.findByItemIdAndStartAfter(anyLong(), any())).thenReturn(nextBooking);
        when(commentRepository.findByItemId(anyLong())).thenReturn(List.of(comment));
        ItemDtoEnlarged itemDtoEnlarged = itemService.getByItemId(1L, 1L);
        assertEquals(itemDtoEnlarged, testItemDtoEnlarged, "Не получили вещь");

    }

    @Test
    void getItemsOfOwner() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        PageImpl<Item> items = new PageImpl<>(List.of(testItem));
        ItemDtoEnlarged testItemDtoEnlarged = new ItemDtoEnlarged(1L, "item", "description", true,
                null, null, null, null);
        when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(items);
        when(bookingRepository.findByItemIdAndStartBefore(anyLong(), any())).thenReturn(null);
        when(bookingRepository.findByItemIdAndStartAfter(anyLong(), any())).thenReturn(null);
        Collection<ItemDtoEnlarged> itemsDto = itemService.getItemsOfOwner(1L, 0, 20);
        assertEquals(itemsDto, List.of(testItemDtoEnlarged), "Не получили вещи хозяина");
    }

    @Test
    void getRequestedItems() {
        User testUser = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                testUser, null, null, null, null);
        ItemDto testItemDto = new ItemDto(1L, "item", "description", true,
                null);
        PageImpl<Item> items = new PageImpl<>(List.of(testItem));
        when(itemRepository.search(anyString(), any())).thenReturn(items);
        Collection<ItemDto> itemsDto = itemService.getRequestedItems("item", 0, 20);
        assertEquals(itemsDto, List.of(testItemDto), "Не получили вещи по поиску");
    }

    @Test
    void addComment() {
        User owner = new User(
                1L,
                "user1",
                "user1@mail.com");
        Item testItem = new Item(1L, "item", "description", true,
                owner, null, null, null, null);
        User author = new User(

                2L,
                "user2",
                "user2@mail.com");
        Booking testBooking = new Booking(1L, LocalDateTime.of(1, 2, 3, 4, 5, 6),
                LocalDateTime.now(), testItem, author, State.APPROVED);
        CommentDto testCommentDto = new CommentDto(2L, "text", author.getName());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(testItem));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(bookingRepository.findByBookerId(anyLong())).thenReturn(List.of(testBooking));
        when(commentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CommentDto commentDto = itemService.addComment(1L, 2L, testCommentDto);
        assertEquals(testCommentDto, commentDto, "Коммент не создался");
    }
}