package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public Item add(Item item) {
        Item newItem = itemStorage.addItem(item);
        log.info("Added item name: {}", item.getName());
        return newItem;
    }

    @Override
    public Item update(Item item) {
        Item newItem = itemStorage.updateItem(item);
        log.info("Updated item id: {}", item.getId());
        return newItem;
    }

    @Override
    public Item getById(long itemId) {
        log.info("Got user id: {}", itemId);
        return itemStorage.getItemById(itemId);
    }

    @Override
    public Collection<Item> getItemsOfOwner(long userId) {
        Collection<Item> itemsDto = itemStorage.getItemsOfOwner(userId);
        log.info("Got items: {}", itemsDto.size());
        return itemsDto;
    }

    @Override
    public Collection<Item> getRequestedItems(String text) {
        Collection<Item> itemsDto = itemStorage.getRequestedItems(text);
        log.info("Got items: {}", itemsDto.size());
        return itemsDto;
    }
}
