package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUpdateItemTest {

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
    void shouldUpdateItemWhenAllFieldsAreValid() {
        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 6L;
        boolean available = true;

        User user = createUserById(userId);

        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto",
                "name base dto",
                requestId,
                available);

        Item item = createItemById(itemId, user);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).then(invocation -> invocation.getArgument(0));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(any())).thenReturn(Collections.emptyList());

        ItemResponseDto result = itemService.updateItem(userId, itemBaseDto, itemId);

        assertNotNull(result);

        assertEquals(itemBaseDto.getName(), result.getName());
        assertEquals(itemBaseDto.getDescription(), result.getDescription());
        assertEquals(itemBaseDto.getAvailable(), result.getAvailable());

        verify(userRepository, times(1))
                .findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository).findFirstByItemIdAndStatusAndEndBefore(
                any(), any(), any(), any()
        );

        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfter(
                any(), any(), any(), any()
        );
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldUpdateItemWhenAllFieldsNull() {
        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 6L;
        boolean available = true;

        User user = createUserById(userId);

        ItemBaseDto itemBaseDto = createItemBaseDto(null,
                null,
                requestId,
                null);

        Item item = createItemById(itemId, user);
        item.setName("Name");
        item.setDescription("Description");
        item.setAvailable(available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).then(invocation -> invocation.getArgument(0));
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(any())).thenReturn(Collections.emptyList());

        ItemResponseDto result = itemService.updateItem(userId, itemBaseDto, itemId);

        assertNotNull(result);

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.isAvailable(), result.getAvailable());

        verify(userRepository, times(1))
                .findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verify(bookingRepository).findFirstByItemIdAndStatusAndEndBefore(
                any(), any(), any(), any()
        );

        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfter(
                any(), any(), any(), any()
        );
        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {
        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 6L;
        boolean available = true;

        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto",
                "name base dto",
                requestId,
                available);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(userId, itemBaseDto, itemId)
        );

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldThrowNotFoundWhenItemNotFound() {
        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 6L;
        boolean available = true;

        User user = createUserById(userId);
        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto",
                "name base dto",
                requestId,
                available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(userId, itemBaseDto, itemId)
        );

        assertEquals("Вещь c ID " + itemId + " не найдена", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldThrowValidationWhenNonOwnerUpdates() {
        Long userId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;

        User user = createUserById(userId);
        User owner = createUserById(ownerId);
        ItemBaseDto itemBaseDto = new ItemBaseDto();

        Item item = createItemById(itemId, owner);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.updateItem(userId, itemBaseDto, itemId)
        );

        assertEquals("Только хозяин вещи может редактировать вещь", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoMoreInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}
