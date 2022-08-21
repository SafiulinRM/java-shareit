package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemStorage {
    ItemDto addItem(ItemDto itemDto, long itemId);

    ItemDto updateItem(ItemDto itemDto, long itemId);

    ItemDto getItemById(long itemId);

    Collection<ItemDto> getItemsOfOwner(long itemId);

    Collection<ItemDto> getRequestedItems(String text);

    boolean isExistById(long id);
}
