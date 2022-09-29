package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.Create;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Collection;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto add(@RequestHeader(USER_ID_HEADER) long userId,
                              @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getById(@PathVariable long requestId,
                                  @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getById(requestId, userId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getRequestsOfUser(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequests(@RequestParam(required = false, defaultValue = "0") @Min(0L) int from,
                                                     @RequestParam(required = false, defaultValue = "10") @Positive int size,
                                                     @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getAllRequests(from, size, userId);
    }
}
