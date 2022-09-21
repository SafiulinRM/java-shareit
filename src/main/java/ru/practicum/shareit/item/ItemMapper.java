package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnlarged;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static Item toItem(ItemDto itemDto, User owner) {
        Item item = new Item();
        item.setId(0);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(owner);
        if (itemDto.getAvailable() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public static Item updateItem(ItemDto itemDto, long itemId) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDtoEnlarged toItemDtoEnlarged(Item item) {
        return new ItemDtoEnlarged(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                item.getLastBooking(),
                item.getNextBooking(),
                item.getComments());
    }

    public static Collection<ItemDtoEnlarged> toItemsDtoEnlarged(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDtoEnlarged)
                .collect(toList());
    }

    public static Collection<ItemDto> toItemsDto(Collection<Item> items) {
        return items.stream()
                .map(ItemMapper::toItemDto)
                .collect(toList());
    }
}