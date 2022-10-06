package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnlarged;

import java.util.Collection;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestBody ItemDto itemDto,
                       @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long itemId) {
        return itemService.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoEnlarged getById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getByItemId(itemId, userId);
    }

    @GetMapping()
    public Collection<ItemDtoEnlarged> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                       @RequestParam(required = false, defaultValue = "0") int from,
                                                       @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.getItemsOfOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDto> getRequestedItems(@RequestParam String text,
                                                 @RequestParam(required = false, defaultValue = "0") int from,
                                                 @RequestParam(required = false, defaultValue = "10") int size) {
        return itemService.getRequestedItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(USER_ID_HEADER) long userId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }
}
