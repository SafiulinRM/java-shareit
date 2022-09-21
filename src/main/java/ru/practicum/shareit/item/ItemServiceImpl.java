package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingShort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.stream.Collectors.toList;
import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentsDto;
import static ru.practicum.shareit.item.ItemMapper.toItem;
import static ru.practicum.shareit.item.ItemMapper.updateItem;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Item add(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found " + userId));
        Item item = toItem(itemDto, owner);
        Item newItem = itemRepository.save(item);
        log.info("Added item name: {}", item.getName());
        return newItem;
    }

    @Override
    @Transactional
    public Item update(ItemDto itemDto, long userId, long itemId) {
        Item updateItem = updateItem(itemDto, itemId);
        Item newItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found " + itemId));
        if (userId != newItem.getOwner().getId()) {
            log.info("Wrong owner id: {}", userId);
            throw new NotFoundException("Wrong owner id: " + userId);
        }
        if (updateItem.getName() != null) {
            newItem.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            newItem.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            newItem.setAvailable(updateItem.getAvailable());
        }
        log.info("Updated item id: {}", itemId);
        return newItem;
    }

    @Override
    public Item getByItemId(long itemId, long userId) {
        Item newItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found " + itemId));
        setBookingsOfItem(newItem, userId);
        Collection<CommentDto> comments = toCommentsDto(commentRepository.findByItemId(itemId));
        newItem.setComments(comments);
        log.info("Got user id: {}", itemId);
        return newItem;
    }

    @Override
    public Collection<Item> getItemsOfOwner(long userId) {
        Collection<Item> itemsDto = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        log.info("Got items: {}", itemsDto.size());
        return itemsDto.stream()
                .map(it -> setBookingsOfItem(it, userId))
                .collect(toList());
    }

    @Override
    public Collection<Item> getRequestedItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Collection<Item> items = itemRepository
                .search(text);
        log.info("Got items: {}", items.size());
        return items;
    }

    @Override
    @Transactional
    public Comment addComment(long itemId, long authorId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("item not found " + itemId));
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found " + item.getOwner().getId()));
        validationComment(authorId, itemId);
        Comment newComment = toComment(commentDto, author, item);
        return commentRepository.save(newComment);
    }

    private void validationComment(long authorId, long itemId) {
        Collection<Booking> bookingsOfAuthor = bookingRepository.findByBookerId(authorId);
        for (Booking booking : bookingsOfAuthor) {
            if (booking.getItem().getId() == itemId &&
                    !booking.getState().equals(State.REJECTED) &&
                    booking.getStart().isBefore(LocalDateTime.now())) {
                return;
            }
        }
        throw new ValidationException("The author did not take the item");
    }

    private Item setBookingsOfItem(Item item, long userId) {
        BookingShort lastBooking = bookingRepository.findByItemIdAndStartBefore(item.getId(), LocalDateTime.now());
        BookingShort nextBooking = bookingRepository.findByItemIdAndStartAfter(item.getId(), LocalDateTime.now());
        if (lastBooking != null) {
            if (lastBooking.getBookerId() != userId) {
                item.setLastBooking(lastBooking);
            }
        }
        if (nextBooking != null) {
            if (nextBooking.getBookerId() != userId) {
                item.setNextBooking(nextBooking);
            }
        }
        return item;
    }
}
