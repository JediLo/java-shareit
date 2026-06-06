package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentMapperDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.ItemMapperDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseMapperDto;
import ru.practicum.shareit.item.model.BookingTime;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemResponseDto> findAllItemsUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        LocalDateTime now = LocalDateTime.now();
        return itemRepository
                .findAllByOwnerId(userId, pageable)
                .getContent()
                .stream()
                .map(item -> getItemResponseDto(item, userId, now))
                .toList();
    }

    @Override
    public ItemResponseDto addNewItem(Long userId, ItemBaseDto itemBaseDto) {

        User user = userRepository.findById(userId).orElseThrow(() -> userNotFound(userId));
        ItemRequest request = null;
        if (itemBaseDto.getRequestId() != null) {
            request = itemRequestRepository
                    .findById(itemBaseDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Переданный Id " + itemBaseDto.getRequestId() + " запроса не существует"));

        }

        Item item = ItemMapperDto.toItem(itemBaseDto, user, request);

        LocalDateTime now = LocalDateTime.now();
        Item saved = itemRepository.save(item);
        return getItemResponseDto(saved, userId, now);
    }

    @Override
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
    public Collection<ItemResponseDto> findItemByText(String text, Long userId, Integer from, Integer size) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemRepository
                .searchAvailableItems(text, pageable)
                .getContent()
                .stream()
                .map(item -> getItemResponseDto(item, userId, now))
                .toList();
    }

    @Override
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
        return CommentMapperDto.toResponseDto(saved);
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
                .map(CommentMapperDto::toResponseDto)
                .toList();
    }

    private ItemResponseDto getItemResponseDto(Item item, Long userId, LocalDateTime now) {

        BookingStatus approved = BookingStatus.APPROVED;
        Sort sortByStartAsc = Sort.by(Sort.Direction.ASC, "start");
        Sort sortByEndDesc = Sort.by(Sort.Direction.DESC, "end");

        BookingTime last = null;
        BookingTime next = null;
        if (item.getOwner().getId().equals(userId)) {
            last = bookingRepository
                    .findFirstByItemIdAndStatusAndEndBefore(item.getId(),
                            approved,
                            now,
                            sortByEndDesc)
                    .map(booking -> new BookingTime(booking.getStart(), booking.getEnd()))
                    .orElse(null);

            next = bookingRepository
                    .findFirstByItemIdAndStatusAndStartAfter(item.getId(),
                            approved,
                            now,
                            sortByStartAsc)
                    .map(booking -> new BookingTime(booking.getStart(), booking.getEnd()))
                    .orElse(null);
        }
        return ItemResponseMapperDto.toItemResponseDto(item, last, next, getComments(item.getId()));
    }
}
