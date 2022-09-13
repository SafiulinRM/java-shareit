package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.item.model.Item;

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

    public static Item toItem(ItemDto itemDto, long ownerId) {
        Item item = new Item();
        item.setId(0);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        if (itemDto.getAvailable() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public static Item updateItem(ItemDto itemDto, long ownerId, long itemId) {
        Item item = new Item();
        item.setId(itemId);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }

    public static ItemDtoInfo toItemDtoInfo(Item item) {
        return new ItemDtoInfo(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId(),
                item.getLastBooking(),
                item.getNextBooking(),
                item.getComments());
    }
}
