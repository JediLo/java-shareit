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
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestFactory.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceGetBookingsByStateForBookerTest {

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    BookingServiceImpl bookingService;

    @Test
    void shouldReturnBookingsForBookerWhenStateIsAll() {

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerId(bookerId, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerId(bookerId, pageable);

    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsPast() {

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBefore(eq(bookerId), any(LocalDateTime.class), eq(pageable));

    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsCurrent() {

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfter(eq(bookerId),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                eq(pageable)))
                .thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartBeforeAndEndAfter(eq(bookerId),
                        any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        eq(pageable));

    }

    @Test
    void shouldReturnBookingsForBookerWhenStateIsFuture() {

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable))).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfter(eq(bookerId), any(LocalDateTime.class), eq(pageable));

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, status, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(bookerId, status, pageable);

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

        when(userRepository.findById(bookerId)).thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndStatus(bookerId, status, pageable)).thenReturn(bookingPage);

        Collection<BookingResponseDto> result = bookingService.getBookingsByStateToBooker(bookerId, bookingState, from, size);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookerId, result.iterator().next().getBooker().getId());

        verify(userRepository, times(1)).findById(bookerId);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatus(bookerId, status, pageable);

    }

    @Test
    void shouldThrowNotFoundWhenBookerNotFound() {
        Long bookerId = 1L;

        when(userRepository.findById(bookerId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.getBookingsByStateToBooker(bookerId, BookingState.PAST, 0, 1)
        );

        assertEquals("Пользователь c ID " + bookerId + " не найден", exception.getMessage());

        verify(bookingRepository, never()).save(any());
    }
}
