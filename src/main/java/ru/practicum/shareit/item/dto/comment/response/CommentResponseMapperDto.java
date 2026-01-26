package ru.practicum.shareit.item.dto.comment.response;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.dto.UserMapperDto;

@UtilityClass
public class CommentResponseMapperDto {
    public static CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponseDto(comment.getId(),
                comment.getText(),
                UserMapperDto.toUserDto(comment.getAuthor()).getName(),
                comment.getCreated());
    }
}
