package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoEnlarged;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.Collection;

import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class}) @RequestBody ItemDto itemDto, @RequestHeader(USER_ID_HEADER) long userId) {
        return toItemDto(itemService.add(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(USER_ID_HEADER) long userId,
                          @PathVariable long itemId) {
        return toItemDto(itemService.update(itemDto, userId, itemId));
    }

    @GetMapping("/{itemId}")
    public ItemDtoEnlarged getById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId) {
        return toItemDtoEnlarged(itemService.getByItemId(itemId, userId));
    }

    @GetMapping()
    public Collection<ItemDtoEnlarged> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long ownerId) {
        return toItemsDtoEnlarged(itemService.getItemsOfOwner(ownerId));
    }

    @GetMapping("/search")
    public Collection<ItemDto> getRequestedItems(@RequestParam String text) {
        return toItemsDto(itemService.getRequestedItems(text));
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(USER_ID_HEADER) long userId,
                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return toCommentDto(itemService.addComment(itemId, userId, commentDto));
    }
}
