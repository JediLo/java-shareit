package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceGetBookingTest {

    @Mock
    BookingRepository bookingRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void shouldReturnBookingWhenRequestedByOwner() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookingId = 4L;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getBooking(bookingId, ownerId);

        assertNotNull(result);

        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldReturnBookingWhenRequestedByBooker() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookingId = 4L;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingResponseDto result = bookingService.getBooking(bookingId, bookerId);

        assertNotNull(result);

        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenBookingDoesNotExist() {

        Long bookingId = 4L;
        Long userId = 5L;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(bookingId, userId)
        );

        assertEquals("Бронирование c ID " + bookingId + " не найден", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationWhenUserIsNotOwnerOrBooker() {
        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookingId = 4L;
        Long userNotBookerAndNotOwnerId = 5L;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.getBooking(bookingId, userNotBookerAndNotOwnerId)
        );

        assertEquals("Запрос может выполнить хозяин вещи или хозяин брони", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

}
