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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceAddStatusBookingTest {

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void shouldApproveBookingWhenOwnerApproves() {
        Long bookingId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookerId = 4L;
        boolean approved = true;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));

        BookingResponseDto result = bookingService.addStatusBooking(approved, bookingId, ownerId);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void shouldRejectBookingWhenOwnerRejects() {
        Long bookingId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookerId = 4L;
        boolean approved = false;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).then(invocation -> invocation.getArgument(0));

        BookingResponseDto result = bookingService.addStatusBooking(approved, bookingId, ownerId);

        assertEquals(BookingStatus.REJECTED, result.getStatus());
        assertEquals(itemId, result.getItem().getId());
        assertEquals(bookerId, result.getBooker().getId());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenBookingNotFound() {
        Long bookingId = 1L;
        Long ownerId = 3L;
        boolean approved = true;

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.addStatusBooking(approved, bookingId, ownerId)
        );

        assertEquals("Бронирование c ID " + bookingId + " не найден", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationWhenUserIsNotOwner() {
        Long bookingId = 1L;
        Long itemId = 2L;
        Long ownerId = 4L;
        Long notOwnerId = 5L;
        Long bookerId = 4L;
        boolean approved = false;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addStatusBooking(approved, bookingId, notOwnerId)
        );

        assertEquals("Только хозяин вещи может подтвердить или отменить заявку.", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldThrowValidationWhenBookingStatusIsNotWaiting() {
        Long bookingId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        Long bookerId = 4L;
        boolean approved = false;

        User owner = createUserById(ownerId);
        User booker = createUserById(bookerId);
        Item item = createItemById(itemId, owner);
        Booking booking = createBooking(item, booker, BookingStatus.APPROVED);

        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.addStatusBooking(approved, bookingId, ownerId)
        );

        assertEquals("На этот предмет нет бронирования, подтвердить или отклонить нельзя", exception.getMessage());

        verify(bookingRepository, times(1)).findById(bookingId);
        verify(bookingRepository, never()).save(any());
    }
}