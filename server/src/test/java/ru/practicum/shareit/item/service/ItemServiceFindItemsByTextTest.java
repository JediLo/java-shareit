package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.item.response.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.createItemById;
import static ru.practicum.shareit.TestFactory.createUserById;

@ExtendWith(MockitoExtension.class)
public class ItemServiceFindItemsByTextTest {
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
    void shouldReturnItemsMatchingText() {
        Long userId = 1L;
        Long itemId = 2L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        String text = "test";

        User user = createUserById(userId);
        Item item = createItemById(itemId, user);
        List<Item> items = List.of(item);
        Page<Item> page = new PageImpl<>(items);

        when(itemRepository.searchAvailableItems(text, pageable)).thenReturn(page);
        when(bookingRepository.findFirstByItemIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfter(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(commentRepository.findAllByItemId(any(Long.class)))
                .thenReturn(Collections.emptyList());

        Collection<ItemResponseDto> result = itemService.findItemByText(text, userId, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.iterator().next().getOwnerId());


        verify(itemRepository, times(1))
                .searchAvailableItems(text, pageable);
        verify(bookingRepository).findFirstByItemIdAndStatusAndEndBefore(
                any(), any(), any(), any()
        );

        verify(bookingRepository).findFirstByItemIdAndStatusAndStartAfter(
                any(), any(), any(), any()
        );

        verify(commentRepository).findAllByItemId(itemId);
    }

    @Test
    void shouldReturnEmptyListWhenNoItemsMatch() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"));
        String text = "test";

        when(itemRepository.searchAvailableItems(text, pageable)).thenReturn(Page.empty());

        Collection<ItemResponseDto> result = itemService.findItemByText(text, userId, from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRepository, times(1))
                .searchAvailableItems(text, pageable);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldReturnEmptyListWhenTextIsBlankOrNull() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        List<String> texts = new ArrayList<>();
        texts.add(null);
        texts.add(" ");
        texts.add("\t");
        texts.add("\n");

        for (String text : texts) {
            Collection<ItemResponseDto> result = itemService.findItemByText(text, userId, from, size);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}
