package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapperDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    // addRequest()

    @Test
    void shouldCreateRequestWhenUserExists() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 3L;
        String description = "description";

        User user = createUserById(userId);
        ItemRequestDto itemRequestDto = createItemRequestDto(description, requestId);
        Item item = createItemById(itemId, user);
        Collection<Item> items = List.of(item);
        ItemRequest itemRequest = ItemRequestMapperDto.toItemRequest(itemRequestDto, user);
        itemRequest.setId(requestId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(items);

        ItemRequestResponseDto result = itemRequestService.addRequest(userId, itemRequestDto);

        assertNotNull(result);
        assertEquals(description, result.getDescription());
        assertEquals(1, result.getItems().size());

        assertEquals(itemId, result.getItems().iterator().next().getItemId());

        verify(userRepository).findById(userId);
        verify(itemRequestRepository).save(any(ItemRequest.class));
        verify(itemRepository).findAllByRequestId(any(Long.class));
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {

        Long userId = 1L;

        ItemRequestDto itemRequestDto = createItemRequestDto("description", userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.addRequest(userId, itemRequestDto)
        );

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(itemRequestRepository);
        verifyNoInteractions(itemRepository);
    }

    // findRequeststByUserId()

    @Test
    void shouldReturnRequestsForUser() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 3L;
        int from = 0;
        int size = 10;

        User user = createUserById(userId);
        ItemRequest itemRequest = createItemRequest("description", user, requestId);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests);
        Item item = createItemById(itemId, user);
        Collection<Item> items = List.of(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(eq(userId), any(Pageable.class))).thenReturn(itemRequestPage);
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(items);

        Collection<ItemRequestResponseDto> result = itemRequestService.findRequestsByUserId(userId, from, size);

        assertNotNull(result);
        assertEquals(itemRequests.size(), result.size());
        ItemRequestResponseDto dto = result.iterator().next();
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemId, dto.getItems().iterator().next().getItemId());

        verify(itemRequestRepository).findByRequestorId(eq(userId), any(Pageable.class));
        verify(itemRepository).findAllByRequestId(any(Long.class));

    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoRequests() {

        Long userId = 1L;
        int from = 0;
        int size = 10;
        User user = createUserById(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(eq(userId), any(Pageable.class))).thenReturn(Page.empty());

        Collection<ItemRequestResponseDto> result = itemRequestService.findRequestsByUserId(userId, from, size);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(itemRequestRepository).findByRequestorId(eq(userId), any(Pageable.class));
        verifyNoInteractions(itemRepository);

    }

    // findAllRequests()

    @Test
    void shouldReturnAllRequests() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 3L;
        int from = 0;
        int size = 10;

        User user = createUserById(userId);
        ItemRequest itemRequest = createItemRequest("description", user, requestId);
        List<ItemRequest> itemRequests = List.of(itemRequest);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(itemRequests);
        Item item = createItemById(itemId, user);
        Collection<Item> items = List.of(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAll(any(Pageable.class))).thenReturn(itemRequestPage);
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        Collection<ItemRequestResponseDto> result = itemRequestService.findAllRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(itemRequests.size(), result.size());
        ItemRequestResponseDto dto = result.iterator().next();
        assertEquals(itemRequest.getDescription(), dto.getDescription());


        verify(itemRequestRepository).findAll(any(Pageable.class));
        verify(itemRepository).findAllByRequestId(requestId);

    }

    @Test
    void shouldReturnEmptyListWhenNoRequestsExist() {

        Long userId = 1L;
        int from = 0;
        int size = 10;
        User user = createUserById(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        Collection<ItemRequestResponseDto> result = itemRequestService.findAllRequests(userId, from, size);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(itemRequestRepository).findAll(any(Pageable.class));
        verifyNoInteractions(itemRepository);

    }

    //findRequestById()

    @Test
    void shouldReturnRequestWhenExists() {

        Long userId = 1L;
        Long itemId = 2L;
        Long requestId = 3L;

        User user = createUserById(userId);
        ItemRequest itemRequest = createItemRequest("description", user, requestId);
        Item item = createItemById(itemId, user);
        Collection<Item> items = List.of(item);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(requestId)).thenReturn(items);

        ItemRequestResponseDto result = itemRequestService.findRequestById(userId, requestId);

        assertNotNull(result);
        assertEquals(itemRequest.getDescription(), result.getDescription());

        verify(itemRequestRepository).findById(requestId);
        verify(itemRepository).findAllByRequestId(requestId);
    }

    @Test
    void shouldReturnNotFoundWhenNoRequestExist() {

        Long userId = 1L;
        Long requestId = 3L;
        User user = createUserById(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemRequestService.findRequestById(userId, requestId)
        );

        assertEquals("Запрос с Id " + requestId + " не найден", exception.getMessage());

        verify(itemRequestRepository).findById(requestId);
        verifyNoInteractions(itemRepository);

    }

}