package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceAddBookingTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldCreateBookingWhenValidData() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        item.setAvailable(true);
        BookingDto bookingDto = createBookingDto(itemId);


        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));

        BookingResponseDto result = bookingService.addBooking(bookingDto, bookerId);

        assertNotNull(result);
        assertEquals(BookingStatus.WAITING, result.getStatus());
        assertEquals(booker.getId(), result.getBooker().getId());
        assertEquals(item.getId(), result.getItem().getId());
        verify(bookingRepository).save(any());
    }


    @Test
    void shouldThrowNotFoundWhenItemNotFound() {

        Long bookerId = 1L;
        Long itemId = 2L;

        BookingDto bookingDto = createBookingDto(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingDto, bookerId)
        );

        assertEquals("Предмет не найден", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenUserNotFound() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;

        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        BookingDto bookingDto = createBookingDto(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addBooking(bookingDto, bookerId)
        );

        assertEquals("Пользователь c ID " + bookerId + " не найден", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationWhenItemNotAvailable() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        item.setAvailable(false);
        BookingDto bookingDto = createBookingDto(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookingDto, bookerId)
        );

        assertEquals("Вещь недоступна", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationWhenUserIsOwner() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 1L;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        item.setAvailable(true);
        BookingDto bookingDto = createBookingDto(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addBooking(bookingDto, bookerId)
        );

        assertEquals("Нельзя забронировать свою вещь", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }


}
