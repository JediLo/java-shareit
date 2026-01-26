package ru.practicum.shareit.booking.dto.response;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemMapperDto;
import ru.practicum.shareit.user.dto.UserMapperDto;

@UtilityClass
public class BookingResponseMapperDto {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingResponseDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapperDto.toItemDto(booking.getItem()),
                UserMapperDto.toUserDto(booking.getBooker()),
                booking.getStatus());
    }
}
