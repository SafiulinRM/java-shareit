package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemService {
    Item add(ItemDto itemDto, long userId);

    Item update(ItemDto itemDto, long userId, long itemId);

    Item getByItemId(long itemId, long userId);

    Collection<Item> getItemsOfOwner(long userId);

    Collection<Item> getRequestedItems(String text);

    Comment addComment(long itemId, long authorId, CommentDto commentDto);
}