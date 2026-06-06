package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.createItemById;
import static ru.practicum.shareit.TestFactory.createUserById;

@ExtendWith(MockitoExtension.class)
public class ItemServiceDeleteItemTest {

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void shouldDeleteItemWhenOwnerDeletes() {

        Long ownerId = 1L;
        Long itemId = 2L;

        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(ownerId, itemId);

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void shouldThrowNotFoundWhenItemNotFound() {
        Long ownerId = 1L;
        Long itemId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.deleteItem(ownerId, itemId)
        );

        assertEquals("Вещь c ID " + itemId + " не найдена", exception.getMessage());

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).deleteById(any());
    }

    @Test
    void shouldThrowValidationWhenNonOwnerDeletes() {
        Long ownerId = 1L;
        Long itemId = 2L;
        Long nonOwnerId = 3L;

        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.deleteItem(nonOwnerId, itemId)
        );

        assertEquals("Только хозяин вещи может ее удалить", exception.getMessage());

        verify(itemRepository, times(1)).findById(itemId);
        verify(itemRepository, never()).deleteById(any());
    }
}
