package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.user.Create;
import ru.practicum.shareit.user.Update;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.CommentMapper.toCommentDto;
import static ru.practicum.shareit.item.ItemMapper.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String HEADER_OF_OWNER = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Validated({Create.class}) @RequestBody ItemDto itemDto, @RequestHeader(HEADER_OF_OWNER) long userId) {
        return toItemDto(itemService.add(toItem(itemDto, userId)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                          @RequestHeader(HEADER_OF_OWNER) long userId, @PathVariable long itemId) {
        return toItemDto(itemService.update(updateItem(itemDto, userId, itemId)));
    }

    @GetMapping("/{itemId}")
    public ItemDtoInfo getById(@PathVariable long itemId, @RequestHeader(HEADER_OF_OWNER) long userId) {
        return toItemDtoInfo(itemService.getByItemId(itemId, userId));
    }

    @GetMapping()
    public Collection<ItemDtoInfo> getItemsOfOwner(@RequestHeader(HEADER_OF_OWNER) long ownerId) {
        return itemService.getItemsOfOwner(ownerId).stream().map(it -> toItemDtoInfo(it)).collect(toList());
    }

    @GetMapping("/search")
    public Collection<ItemDto> getRequestedItems(@RequestParam String text) {
        return itemService.getRequestedItems(text).stream().map(it -> toItemDto(it)).collect(toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable long itemId,
                                 @RequestHeader(HEADER_OF_OWNER) long userId,
                                 @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return toCommentDto(itemService.addComment(itemId, userId, commentDto));
    }
}
