package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnlarged;

import java.util.Collection;

public interface ItemService {
    ItemDto add(ItemDto itemDto, long userId);

    ItemDto update(ItemDto itemDto, long userId, long itemId);

    ItemDtoEnlarged getByItemId(long itemId, long userId);

    Collection<ItemDtoEnlarged> getItemsOfOwner(long userId, int from, int size);

    Collection<ItemDto> getRequestedItems(String text, int from, int size);

    CommentDto addComment(long itemId, long authorId, CommentDto commentDto);
}