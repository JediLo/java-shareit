package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceGetBookingsByStateForOwnerTest {
    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void shouldReturnBookingsForOwnerWhenStateIsAll() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.ALL;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(owner));
        when(bookingRepository.findAllByItemOwnerId(ownerId, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerId(ownerId, pageable);

    }

    @Test
    void shouldReturnBookingsForOwnerWhenStateIsPast() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.PAST;


        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndEndBefore(eq(ownerId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndEndBefore(eq(ownerId), any(LocalDateTime.class), eq(pageable));

    }

    @Test
    void shouldReturnBookingsForOwnerWhenStateIsCurrent() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.CURRENT;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfter(eq(ownerId),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable)))
                .thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartBeforeAndEndAfter(eq(ownerId),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        eq(pageable));

    }

    @Test
    void shouldReturnBookingsForOwnerWhenStateIsFuture() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.FUTURE;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfter(eq(ownerId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStartAfter(eq(ownerId), any(LocalDateTime.class), eq(pageable));

    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsWaiting() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.WAITING;
        BookingStatus status = BookingStatus.WAITING;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, status, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(ownerId, status, pageable);

    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsRejected() {

        Long bookerId = 1L;
        Long itemId = 2L;
        Long ownerId = 3L;
        int from = 0;
        int size = 10;
        BookingState bookingState = BookingState.REJECTED;
        BookingStatus status = BookingStatus.REJECTED;

        User booker = createUserById(bookerId);
        User owner = createUserById(ownerId);
        Item item = createItemById(itemId, owner);
        Booking bookingWaiting = createBooking(item, booker, BookingStatus.WAITING);
        List<Booking> bookings = new ArrayList<>();
        bookings.add(bookingWaiting);
        Page<Booking> bookingPage = new PageImpl<>(bookings);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));

        when(userRepository.findById(ownerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, status, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToOwner(ownerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(ownerId);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerIdAndStatus(ownerId, status, pageable);

    }

    @Test
    void shouldThrowNotFoundWhenBookerNotFound() {
        Long ownerId = 1L;

        when(userRepository.findById(ownerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsByStateToOwner(ownerId, BookingState.PAST, 0, 1)
        );

        assertEquals("Пользователь c ID " + ownerId + " не найден", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }
}
