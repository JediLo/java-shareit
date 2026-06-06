package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.createItemById;
import static ru.practicum.shareit.TestFactory.createUserById;

@ExtendWith(MockitoExtension.class)
public class ItemServiceFindItemByIdTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void shouldReturnItemWhenExists() {
        Long userId = 1L;
        Long itemId = 2L;

        User user = createUserById(userId);
        Item item = createItemById(itemId, user);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
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

        ItemResponseDto result = itemService.findItemById(itemId, userId);

        assertNotNull(result);
        assertEquals(userId, result.getOwnerId());

        verify(itemRepository, times(1))
                .findById(itemId);
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
    void shouldThrowNotFoundWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.findItemById(itemId, userId)
        );

        assertEquals("Вещь c ID " + itemId + " не найдена", exception.getMessage());

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}
