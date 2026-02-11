package ru.practicum.shareit.item.dto.item.response;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.comment.CommentResponseDto;
import ru.practicum.shareit.item.model.BookingTime;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

@UtilityClass
public class ItemResponseMapperDto {
    public static ItemResponseDto toItemResponseDto(Item item,
                                                    BookingTime last,
                                                    BookingTime next,
                                                    Collection<CommentResponseDto> comments) {
        if (item == null) {
            return null;
        }
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }
}
