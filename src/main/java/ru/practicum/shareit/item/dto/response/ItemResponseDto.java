package ru.practicum.shareit.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.comment.response.CommentResponseDto;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.item.model.BookingTime;

import java.util.Collection;


@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)

public class ItemResponseDto extends ItemBaseDto {

    private BookingTime lastBooking;
    private BookingTime nextBooking;
    private Collection<CommentResponseDto> comments;
}
