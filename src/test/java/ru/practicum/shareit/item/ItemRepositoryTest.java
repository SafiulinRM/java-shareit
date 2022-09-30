package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;
    User user;
    Item item;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(new User(1L, "pit", "some@email.com"));
        item = itemRepository.save(new Item(1L, "item", "description", true,
                user, null, null, null, null));
    }

    @AfterEach
    void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void search() {
        final Page<Item> items = itemRepository.search("item", Pageable.unpaged());
        assertNotNull(items);
        assertEquals(1, items.getTotalElements());
    }
}