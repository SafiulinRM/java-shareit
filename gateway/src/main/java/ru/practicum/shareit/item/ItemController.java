package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@Validated({Create.class}) @RequestBody ItemDto itemDto,
                                      @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.add(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Validated({Update.class}) @RequestBody ItemDto itemDto,
                                         @RequestHeader(USER_ID_HEADER) long userId,
                                         @PathVariable long itemId) {
        return itemClient.update(itemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable long itemId, @RequestHeader(USER_ID_HEADER) long userId) {
        return itemClient.getById(itemId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getItemsOfOwner(@RequestHeader(USER_ID_HEADER) long ownerId,
                                                  @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                  @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return itemClient.getItemsOfOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getRequestedItems(@RequestHeader(USER_ID_HEADER) long userId,
                                                    @RequestParam String text,
                                                    @PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                    @Positive @RequestParam(required = false, defaultValue = "10") int size) {
        return itemClient.getRequestedItems(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @RequestHeader(USER_ID_HEADER) long userId,
                                             @Validated({Create.class}) @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
