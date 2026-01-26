package ru.practicum.shareit.item.dto.comment.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.item.ItemBaseDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentResponseDto {

    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
