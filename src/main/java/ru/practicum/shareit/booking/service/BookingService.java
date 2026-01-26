package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingResponseDto addBooking(BookingDto bookingDto, Long userId);

    BookingResponseDto addStatusBooking(boolean approved, Long bookingId, Long userId);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    Collection<BookingResponseDto> getBookingsByStateToBooker(Long userId, BookingState state);

    Collection<BookingResponseDto> getBookingsByStateToOwner(Long userId, BookingState state);
}
