package ru.practicum.shareit.item.dto.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapperDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapperDto {

    public static CommentResponseDto toResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentResponseDto(comment.getId(),
                comment.getText(),
                UserMapperDto.toUserDto(comment.getAuthor()).getName(),
                comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto, Item item, User author) {
        if (commentDto == null) {
            return null;
        }
        return new Comment(commentDto.getText(),
                item,
                author);
    }
}
