package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapperDto {
    public Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        if (bookingDto == null) {
            return null;
        }
        return new Booking(bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                booker,
                bookingDto.getStatus());

    }

    public BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }


}
