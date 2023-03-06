package ru.practicum.ewm.service.comment;

import ru.practicum.ewm.model.comment.dto.*;

import java.util.Collection;

public interface CommentService {

    Collection<CommentShortDto> getAllEventsComments(long userId, long eventId, int from, int size);

    CommentFullDto getCommentByIdForAuthor(long userId, long comId);

    CommentShortDto createComment(long userId, long eventId, NewCommentDto newCommentDto);

    CommentShortDto updateCommentByUser(long userId, long comId, CommentUpdateUserRequest commentDto);

    void deleteCommentByUser(long userId, long comId);

    Collection<CommentFullDto> getAllCommentsForAuthor(long userId, int from, int size);

    Collection<CommentShortDto> getAllEventsCommentsForPublic(long eventId, int from, int size);

    Collection<CommentFullDto> getAllCommentsEventByAdmin(long eventId, int from, int size);

    CommentFullDto getCommentByIdForAdmin(long comId);

    CommentFullDto updateCommentByAdmin(long comId, CommentUpdateAdminRequest commentDto);

    void deleteCommentByAdmin(long comId);
}
