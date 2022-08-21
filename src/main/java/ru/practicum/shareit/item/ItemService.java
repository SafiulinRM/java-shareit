package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDto getById(long itemId);

    Collection<ItemDto> getItemsOfOwner(long userId);

    Collection<ItemDto> getRequestedItems(String text);
}