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
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceAddNewItemTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImpl itemService;

    @Test
    void shouldCreateItemWhenValidData() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requesterId = 5L;
        Long requestId = 6L;
        boolean available = true;

        User user = createUserById(userId);
        User requester = createUserById(requesterId);

        ItemRequest request = createItemRequest("description request", requester, requestId);
        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto",
                "name base dto",
                requestId,
                available);
        Item item = createItemById(itemId, user);
        item.setName(itemBaseDto.getName());
        item.setDescription(itemBaseDto.getDescription());
        item.setRequest(request);
        item.setAvailable(available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(request));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
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


        ItemResponseDto result = itemService.addNewItem(userId, itemBaseDto);

        assertNotNull(result);
        assertEquals(userId, result.getOwnerId());
        assertEquals(itemBaseDto.getName(), result.getName());
        assertEquals(itemBaseDto.getDescription(), result.getDescription());
        assertEquals(itemBaseDto.getAvailable(), result.getAvailable());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemRequestRepository, times(1)).findById(requestId);

    }

    @Test
    void shouldCreateItemWhenRequestIdIsNull() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = null;
        boolean available = true;

        User user = createUserById(userId);
        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto",
                "name base dto",
                requestId,
                available);
        Item item = createItemById(itemId, user);
        item.setName(itemBaseDto.getName());
        item.setDescription(itemBaseDto.getDescription());
        item.setAvailable(available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(itemRepository.save(any(Item.class))).thenReturn(item);
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


        ItemResponseDto result = itemService.addNewItem(userId, itemBaseDto);

        assertNotNull(result);
        assertEquals(userId, result.getOwnerId());
        assertEquals(itemBaseDto.getName(), result.getName());
        assertEquals(itemBaseDto.getDescription(), result.getDescription());
        assertEquals(itemBaseDto.getAvailable(), result.getAvailable());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).save(any(Item.class));
        verifyNoInteractions(itemRequestRepository);

    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {

        Long userId = 1L;
        ItemBaseDto itemBaseDto = new ItemBaseDto();


        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addNewItem(userId, itemBaseDto)
        );

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemRequestRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldThrowNotFoundWhenRequestNotFound() {
        Long userId = 1L;
        Long requestId = 2L;
        boolean available = true;

        User user = createUserById(userId);

        ItemBaseDto itemBaseDto = createItemBaseDto("description base dto", "name base dto", requestId, available);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addNewItem(userId, itemBaseDto)
        );

        assertEquals("Переданный Id " + requestId + " запроса не существует", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verify(itemRequestRepository, times(1)).findById(requestId);
        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);

    }
}
