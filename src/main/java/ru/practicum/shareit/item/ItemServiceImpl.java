package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public ItemDto add(ItemDto itemDto, long userId) {
        ItemDto newItemDto = itemStorage.addItem(itemDto, userId);
        log.info("Added item name: {}", itemDto.getName());
        return newItemDto;
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId, long itemId) {
        checkItemExist(itemId);
        itemDto.setId(itemId);
        ItemDto newItemDto = itemStorage.updateItem(itemDto, userId);
        log.info("Updated item id: {}", itemDto.getId());
        return newItemDto;
    }

    @Override
    public ItemDto getById(long itemId) {
        checkItemExist(itemId);
        log.info("Got user id: {}", itemId);
        return itemStorage.getItemById(itemId);
    }

    @Override
    public Collection<ItemDto> getItemsOfOwner(long userId) {
        Collection<ItemDto> itemsDto = itemStorage.getItemsOfOwner(userId);
        log.info("Got items: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Collection<ItemDto> getRequestedItems(String text) {
        Collection<ItemDto> itemsDto = itemStorage.getRequestedItems(text);
        log.info("Got items: {}", itemsDto.size());
        return itemsDto;
    }

    private void checkItemExist(long id) {
        if (!itemStorage.isExistById(id)) {
            log.warn("Item with id: {} doesn't exist!", id);
            throw new NotFoundException(String.format("Item with id: %d doesn't exist!", id));
        }
    }
}
