package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceFindAllItemsUserTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void shouldReturnItemsForUserWhenExists() {
        Long userId = 1L;
        Long itemId = 2L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        User user = createUserById(userId);
        Item item = createItemById(itemId, user);
        List<Item> items = List.of(item);
        Page<Item> page = new PageImpl<>(items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(page);
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBefore(any(Long.class),
                any(BookingStatus.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(any(Long.class),
                any(BookingStatus.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.iterator().next().getOwnerId());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1))
                .findAllByOwnerId(userId, pageable);
        verify(bookingRepository).findFirstByItemIdAndStatusAndEndBefore(
                eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class),
                any(Sort.class)
        );

        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfter(
                eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class),
                any(Sort.class)
        );

        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoItems() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        User user = createUserById(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(Page.empty());

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(userId, from, size);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1))
                .findAllByOwnerId(userId, pageable);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {
        Long userId = 1L;
        int from = 0;
        int size = 10;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findAllItemsUser(userId, from, size)
        );

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, never()).save(any());
    }

    @Test
    void shouldIncludeLastAndNextBookingForOwner() {
        Long userId = 1L;
        Long itemId = 2L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        User user = createUserById(userId);
        Item item = createItemById(itemId, user);

        Booking bookingLast = createBooking(item, user, BookingStatus.APPROVED);
        Booking bookingNext = createBooking(item, user, BookingStatus.APPROVED);

        List<Item> items = List.of(item);
        Page<Item> page = new PageImpl<>(items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(page);
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBefore(eq(itemId),
                any(BookingStatus.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(Optional.of(bookingLast));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(eq(itemId),
                any(BookingStatus.class),
                any(LocalDateTime.class),
                any(Sort.class))).thenReturn(Optional.of(bookingNext));
        when(commentRepository.findAllByItemId(itemId))
                .thenReturn(Collections.emptyList());

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(userId, from, size);
        ItemResponseDto itemResponseDto = result.iterator().next();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.iterator().next().getOwnerId());
        assertNotNull(itemResponseDto.getNextBooking());
        assertNotNull(itemResponseDto.getLastBooking());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1))
                .findAllByOwnerId(userId, pageable);
        verify(bookingRepository).findFirstByItemIdAndStatusAndEndBefore(
                eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class),
                any(Sort.class)
        );

        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfter(
                eq(itemId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class),
                any(Sort.class)
        );
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldNotIncludeBookingsForNonOwner() {
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        User user = createUserById(userId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        List<Item> items = List.of(item);
        Page<Item> page = new PageImpl<>(items);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(page);
        when(commentRepository.findAllByItemId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ownerId, result.iterator().next().getOwnerId());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1))
                .findAllByOwnerId(userId, pageable);
        verifyNoInteractions(bookingRepository);

        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldIncludeCommentsForItems() {
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long commentId = 4L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));

        User user = createUserById(userId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        List<Item> items = List.of(item);
        Page<Item> page = new PageImpl<>(items);
        Comment comment = createComment(commentId, owner, "Comment text");
        Collection<Comment> comments = List.of(comment);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findAllByOwnerId(userId, pageable)).thenReturn(page);
        when(commentRepository.findAllByItemId(itemId))
                .thenReturn(comments);

        Collection<ItemResponseDto> result = itemService.findAllItemsUser(userId, from, size);
        ItemResponseDto itemResponseDto = result.iterator().next();
        assertFalse(itemResponseDto.getComments().isEmpty());
        CommentResponseDto commentResponseDto = itemResponseDto.getComments().iterator().next();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ownerId, itemResponseDto.getOwnerId());
        assertEquals(commentId, commentResponseDto.getId());
        assertEquals(owner.getName(), commentResponseDto.getAuthorName());
        assertEquals(comment.getText(), commentResponseDto.getText());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1))
                .findAllByOwnerId(userId, pageable);
        verifyNoInteractions(bookingRepository);

        verify(commentRepository).findAllByItemId(itemId);
    }

}