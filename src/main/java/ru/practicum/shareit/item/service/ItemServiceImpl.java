package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapperDto;
import ru.practicum.shareit.item.dto.comment.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.comment.response.CommentResponseMapperDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.ItemMapperDto;
import ru.practicum.shareit.item.dto.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.response.ItemResponseMapperDto;
import ru.practicum.shareit.item.model.BookingTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponseDto> findAllItemsUser(Long userId) {

        userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));

        Collection<Item> items = itemRepository.findAllByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<ItemResponseDto> itemResponse = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        items.forEach(item -> itemResponse.add(getItemResponseDto(item, userId, now)));

        return itemResponse;
    }

    @Override
    @Transactional
    public ItemResponseDto addNewItem(Long userId, ItemBaseDto itemBaseDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));
        Item item = ItemMapperDto.toItem(itemBaseDto, user, null);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Id хозяина вещи и ID пользователя должны быть идентичны.");
        }
        LocalDateTime now = LocalDateTime.now();
        Item saved = itemRepository.save(item);
        return getItemResponseDto(saved, userId, now);
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> itemNotFound(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Только хозяин вещи может ее удалить");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto findItemById(Long itemId, Long userId) {

        Item item = itemRepository
                .findById(itemId)
                .orElseThrow(() -> itemNotFound(itemId));
        LocalDateTime now = LocalDateTime.now();
        return getItemResponseDto(item, userId, now);
    }


    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponseDto> findItemByText(String text, Long userId) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<Item> items = itemRepository
                .findByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(text, text);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<ItemResponseDto> itemResponse = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        items.forEach(item -> itemResponse.add(getItemResponseDto(item, userId, now)));
        return itemResponse;
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Long userId, ItemBaseDto itemBaseDto, Long itemId) {

        userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));

        Item item = itemRepository.findById(itemId).orElseThrow(() -> itemNotFound(itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("Только хозяин вещи может редактировать вещь");
        }
        if (itemBaseDto.getName() != null) {
            item.setName(itemBaseDto.getName());
        }
        if (itemBaseDto.getDescription() != null) {
            item.setDescription(itemBaseDto.getDescription());
        }
        if (itemBaseDto.getAvailable() != null) {
            item.setAvailable(itemBaseDto.getAvailable());
        }
        LocalDateTime now = LocalDateTime.now();
        Item saved = itemRepository.save(item);
        return getItemResponseDto(saved, userId, now);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentDto commentDto, Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> itemNotFound(itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));
        LocalDateTime now = LocalDateTime.now();
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId,
                userId,
                BookingStatus.APPROVED,
                now)) {
            throw new ValidationException("Комментарии можно писать только под той вещью, которую вы уже брали в аренду");
        }
        Comment saved = commentRepository.save(CommentMapperDto.toComment(commentDto, item, user));
        return CommentResponseMapperDto.toResponseDto(saved);
    }

    private NotFoundException userNotFound(Long userId) {
        throw new NotFoundException("Пользователь c ID " + userId + " не найден");
    }

    private NotFoundException itemNotFound(Long itemId) {
        throw new NotFoundException("Вещь c ID " + itemId + " не найдена");
    }

    private Collection<CommentResponseDto> getComments(Long itemId) {
        return commentRepository
                .findAllByItemId(itemId)
                .stream()
                .map(CommentResponseMapperDto::toResponseDto)
                .toList();
    }

    private ItemResponseDto getItemResponseDto(Item item, Long userId, LocalDateTime now) {

        BookingStatus approved = BookingStatus.APPROVED;
        BookingTime last = null;
        BookingTime next = null;
        if (item.getOwner().getId().equals(userId)) {
            last = bookingRepository
                    .findFirstByItemIdAndStatusAndEndBeforeOrderByEndDesc(item.getId(),
                            approved,
                            now)
                    .map(booking -> new BookingTime(booking.getStart(), booking.getEnd()))
                    .orElse(null);

            next = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                            approved,
                            now)
                    .map(booking -> new BookingTime(booking.getStart(), booking.getEnd()))
                    .orElse(null);
        }
        return ItemResponseMapperDto.toItemResponseDto(item, last, next, getComments(item.getId()));
    }
}
