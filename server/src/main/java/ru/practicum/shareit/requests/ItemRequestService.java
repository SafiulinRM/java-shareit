package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.ItemMapper.toItemsDto;
import static ru.practicum.shareit.requests.RequestMapper.toItemRequestDto;
import static ru.practicum.shareit.requests.RequestMapper.toRequest;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestService {
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "created");
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ItemRequestDto add(long userId, ItemRequestDto itemRequestDto) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found " + userId));
        Request request = toRequest(itemRequestDto, requester);
        Request saveRequest = itemRequestRepository.save(request);
        log.info("Added request id: {}", request.getId());
        validationUserExist(userId);
        return toItemRequestDto(saveRequest, null);
    }

    public ItemRequestDto getById(long requestId, long userId) {
        Request request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found " + requestId));
        return toRequestOutput(request, userId);
    }

    private ItemRequestDto toRequestOutput(Request request, long userId) {
        validationUserExist(userId);
        Collection<ItemDto> items = toItemsDto(itemRepository.findByRequestId(request.getId()));
        return toItemRequestDto(request, items);
    }

    public Collection<ItemRequestDto> getRequestsOfUser(long userId) {
        validationUserExist(userId);
        Collection<Request> requests = itemRequestRepository.findByRequesterId(userId);
        return requests.stream()
                .map(request -> toRequestOutput(request, userId))
                .collect(toList());
    }

    public Collection<ItemRequestDto> getAllRequests(int from, int size, long userId) {
        validationUserExist(userId);
        if (from == 0 && size == 0) {
            throw new ValidationException("from: " + from + " or size: " + size + " is null");
        }
        int page = from / size;
        Page<Request> requests = itemRequestRepository
                .findAllByRequesterIdNot(PageRequest.of(page, size, DEFAULT_SORT), userId);
        return requests.stream()
                .map(request -> toRequestOutput(request, userId))
                .collect(toList());
    }

    private void validationUserExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found " + userId);
        }
    }
}
