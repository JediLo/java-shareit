package ru.practicum.shareit.item.dto.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapperDto {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDto(comment.getId(),
                comment.getText());
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
