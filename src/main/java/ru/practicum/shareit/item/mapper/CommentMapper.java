package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto mapCommentToCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getItem().getId(),
                comment.getAuthor().getName(),
                comment.getText(),
                comment.getCreated()
        );
    }

    public static Comment mapCommentDtoToComment(User user, Item item, CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());

        return comment;
    }
}
