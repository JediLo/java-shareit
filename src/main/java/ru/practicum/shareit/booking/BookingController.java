package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@Valid @RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto addStatusBooking(@RequestParam("approved") boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                               @PathVariable("bookingId") Long bookingId) {
        return bookingService.addStatusBooking(approved, bookingId, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getBookingsByStateToBooker(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                                     @RequestParam(value = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByStateToBooker(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getBookingsByStateToOwner(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                                    @RequestParam(value = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsByStateToOwner(userId, state);
    }
}
