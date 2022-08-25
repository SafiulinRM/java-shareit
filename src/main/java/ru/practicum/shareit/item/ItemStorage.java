package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemStorage {
    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    Collection<Item> getItemsOfOwner(long itemId);

    Collection<Item> getRequestedItems(String text);
}
