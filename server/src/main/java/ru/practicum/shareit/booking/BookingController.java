package ru.practicum.shareit.booking;

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

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@PathVariable("bookingId") Long bookingId,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingResponseDto> getBookingsByStateToBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                     @RequestParam(value = "state") BookingState state,
                                                                     @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                     @RequestParam(name = "size", defaultValue = "10") Integer size) {

        return bookingService.getBookingsByStateToBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingResponseDto> getBookingsByStateToOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                    @RequestParam(value = "state") BookingState state,
                                                                    @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                                    @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getBookingsByStateToOwner(userId, state, from, size);
    }

    @PostMapping
    public BookingResponseDto createBooking(@RequestBody BookingDto bookingDto,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.addBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto addStatusBooking(@RequestParam("approved") boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable("bookingId") Long bookingId) {
        return bookingService.addStatusBooking(approved, bookingId, userId);
    }

}
