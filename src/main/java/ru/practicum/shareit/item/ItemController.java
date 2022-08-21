package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.Collection;

/**
 * // TODO .
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class}) @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable long itemId) {
        return itemService.getById(itemId);
    }

    @GetMapping()
    public Collection<ItemDto> getItemsOfOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsOfOwner(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getRequestedItems(@RequestParam String text) {
        return itemService.getRequestedItems(text);
    }
}
