package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Validated
@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    public static final String USER_ID_HEADER = "X-Sharer-User-Id";

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID_HEADER) long userId,
                                      @Validated({Create.class}) @RequestBody ItemRequestDto itemRequestDto) {
        return requestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable long requestId,
                                          @RequestHeader(USER_ID_HEADER) long userId) {
        return requestClient.getById(requestId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsOfUser(@RequestHeader(USER_ID_HEADER) long userId) {
        return requestClient.getRequestsOfUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(required = false, defaultValue = "0") int from,
                                                 @Positive @RequestParam(required = false, defaultValue = "10") int size,
                                                 @RequestHeader(USER_ID_HEADER) long userId) {
        return requestClient.getAllRequests(from, size, userId);
    }
}
