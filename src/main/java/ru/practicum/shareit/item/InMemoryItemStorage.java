package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto addItem(ItemDto itemDto, long ownerId) {
        if (!InMemoryUserStorage.getUsers().containsKey(ownerId)) {
            log.warn("User with id: {} not found!", ownerId);
            throw new NotFoundException("User with id: " + ownerId + " not found!");
        }
        Item item = toItem(itemDto, ownerId);
        items.put(item.getId(), item);
        return getItemById(item.getId());
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long ownerId) {
        checkOwner(itemDto, ownerId);
        Item item = items.get(itemDto.getId());
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return getItemById(item.getId());
    }

    @Override
    public ItemDto getItemById(long itemId) {
        return toItemDto(items.get(itemId));
    }

    @Override
    public Collection<ItemDto> getItemsOfOwner(long ownerId) {
        Collection<ItemDto> itemsOfOwner = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == ownerId) {
                itemsOfOwner.add(toItemDto(item));
            }
        }
        return itemsOfOwner;
    }

    @Override
    public Collection<ItemDto> getRequestedItems(String text) {
        Collection<ItemDto> requestedItems = new ArrayList<>();
        if (text.isBlank()) {
            return requestedItems;
        }
        StringBuilder request = new StringBuilder(text.toLowerCase());
        for (Item item : items.values()) {
            StringBuilder nameInLowerCase = new StringBuilder(item.getName().toLowerCase());
            StringBuilder descriptionInLowerCase = new StringBuilder(item.getDescription().toLowerCase());
            if ((nameInLowerCase.toString().contains(request.toString()) ||
                    descriptionInLowerCase.toString().contains(request.toString()))
                    && item.isAvailable()) {
                requestedItems.add(toItemDto(item));
            }
        }
        return requestedItems;
    }

    @Override
    public boolean isExistById(long itemId) {
        return items.containsKey(itemId);
    }

    private void checkOwner(ItemDto itemDto, long ownerId) {
        if (items.get(itemDto.getId()).getOwner() != ownerId) {
            log.warn("Item update with other user: {}", ownerId);
            throw new NotFoundException("Item update with other user: " + ownerId);
        }
    }
}
