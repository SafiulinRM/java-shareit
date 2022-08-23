package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.IdGenerator.generateItemId;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Item addItem(Item item) {
        if (!InMemoryUserStorage.getUsers().containsKey(item.getOwner())) {
            log.warn("User with id: {} not found!", item.getOwner());
            throw new NotFoundException("User with id: " + item.getOwner() + " not found!");
        }
        item.setId(generateItemId());
        items.put(item.getId(), item);
        return getItemById(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        checkOwner(item);
        Item updateItem = items.get(item.getId());
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return getItemById(item.getId());
    }

    @Override
    public Item getItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            log.warn("Item with id: {} doesn't exist!", itemId);
            throw new NotFoundException(String.format("Item with id: %d doesn't exist!", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getItemsOfOwner(long ownerId) {
        return items.values().stream().filter(it -> it.getOwner() == ownerId).collect(toList());
    }

    @Override
    public Collection<Item> getRequestedItems(String text) {
        Collection<Item> requestedItems = new ArrayList<>();
        if (text.isBlank()) {
            return requestedItems;
        }
        StringBuilder request = new StringBuilder(text.toLowerCase());
        for (Item item : items.values()) {
            StringBuilder nameInLowerCase = new StringBuilder(item.getName().toLowerCase());
            StringBuilder descriptionInLowerCase = new StringBuilder(item.getDescription().toLowerCase());
            if ((nameInLowerCase.toString().contains(request.toString()) ||
                    descriptionInLowerCase.toString().contains(request.toString()))
                    && item.getAvailable()) {
                requestedItems.add(item);
            }
        }
        return requestedItems;
    }

    private void checkOwner(Item item) {
        if (items.get(item.getId()).getOwner() != item.getOwner()) {
            log.warn("Item update with other user: {}", item.getOwner());
            throw new NotFoundException("Item update with other user: " + item.getOwner());
        }
    }
}
