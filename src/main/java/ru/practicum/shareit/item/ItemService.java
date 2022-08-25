package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item add(Item item);

    Item update(Item item);

    Item getById(long itemId);

    Collection<Item> getItemsOfOwner(long userId);

    Collection<Item> getRequestedItems(String text);
}