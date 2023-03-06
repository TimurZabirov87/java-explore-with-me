package ru.practicum.ewm.model.comment.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.comment.entity.Comment;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.dto.UserMapper;
import ru.practicum.ewm.model.user.entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {

    public static CommentFullDto toCommentFullDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentFullDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                UserMapper.userToUserShortDto(comment.getAuthor()),
                comment.getState(),
                comment.getCreated(),
                comment.getPublished()
                );
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentShortDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getAuthor().getName(),
                comment.getPublished());

    }

    public static Comment toComment(NewCommentDto newCommentDto, User author, Event event) {
        if (newCommentDto == null) {
            return null;
        }

        return new Comment(
                null,
                newCommentDto.getText(),
                event,
                author,
                LocalDateTime.now().withNano(0),
                CommentState.PENDING,
                null);
    }
}
