package ru.practicum.shareit.requests;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static Request toRequest(ItemRequestDto itemRequestDto, User requester) {
        return new Request(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requester);
    }

    public static ItemRequestDto toItemRequestDto(Request request, Collection<ItemDto> items) {
        return new ItemRequestDto(request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated(),
                items
        );
    }
}
