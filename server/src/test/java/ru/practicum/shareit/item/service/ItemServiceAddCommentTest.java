package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.createItemById;
import static ru.practicum.shareit.TestFactory.createUserById;

@ExtendWith(MockitoExtension.class)
public class ItemServiceAddCommentTest {

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
    void shouldCreateCommentWhenUserHasBookedItem() {
        Long itemId = 1L;
        Long userId = 2L;
        Long commentId = 3L;

        User user = createUserById(userId);
        user.setName("User Name");
        Item item = createItemById(itemId, user);
        CommentDto commentDto = new CommentDto(commentId, "comment");


        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(itemId),
                eq(userId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(true);
        when(commentRepository.save(any())).then(invocation -> invocation.getArgument(0));

        CommentResponseDto result = itemService.addComment(commentDto, itemId, userId);

        assertNotNull(result);
        assertEquals(user.getName(), result.getAuthorName());
        assertEquals(commentDto.getText(), result.getText());

        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any());

    }

    @Test
    void shouldThrowValidationWhenUserHasNotBookedItem() {
        Long itemId = 1L;
        Long userId = 2L;
        Long commentId = 3L;

        User user = createUserById(userId);
        user.setName("User Name");
        Item item = createItemById(itemId, user);
        CommentDto commentDto = new CommentDto(commentId, "comment");

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(itemId),
                eq(userId),
                eq(BookingStatus.APPROVED),
                any(LocalDateTime.class))).thenReturn(false);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addComment(commentDto, itemId, userId)
        );

        assertEquals("Комментарии можно писать только под той вещью, которую вы уже брали в аренду", exception.getMessage());


        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).findById(userId);
        verify(bookingRepository, times(1)).existsByItemIdAndBookerIdAndStatusAndEndBefore(
                eq(itemId), eq(userId), eq(BookingStatus.APPROVED), any(LocalDateTime.class));
        verifyNoInteractions(commentRepository);

    }

    @Test
    void shouldThrowNotFoundWhenItemNotFound() {

        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(commentDto, itemId, userId)
        );

        assertEquals("Вещь c ID " + itemId + " не найдена", exception.getMessage());

        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {

        Long itemId = 1L;
        Long userId = 2L;
        CommentDto commentDto = new CommentDto();
        Item item = new Item();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.addComment(commentDto, itemId, userId)
        );

        assertEquals("Пользователь c ID " + userId + " не найден", exception.getMessage());

        verify(itemRepository, times(1)).findById(itemId);
        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(commentRepository);
    }
}
