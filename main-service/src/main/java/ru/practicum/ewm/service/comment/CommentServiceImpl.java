package ru.practicum.ewm.service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.comment.CommentStateActionAdmin;
import ru.practicum.ewm.model.comment.dto.*;
import ru.practicum.ewm.model.comment.entity.Comment;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.entity.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.PageMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    public Collection<CommentShortDto> getAllEventsComments(long userId, long eventId, int from, int size) {

        User initiator = getUserById(userId);
        Event event = getEventById(eventId);

        checkThatEventPublished(event);

        return commentRepository.getAllByEventAndState(event,
                                                          CommentState.PUBLISHED,
                                                          PageMapper.getPagable(from, size, "published"))
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto getCommentByIdForAuthor(long userId, long comId) {

        User author = getUserById(userId);
        Comment comment = getCommentById(comId);

        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new NoSuchCommentException("Comment with id=" + comId + " for user with id=" + userId + " not found.");
        }

        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public CommentShortDto createComment(long userId, long eventId, NewCommentDto newCommentDto) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        checkThatEventPublished(event);

        return CommentMapper.toCommentShortDto(commentRepository.save(CommentMapper.toComment(newCommentDto, author, event)));
    }

    @Transactional
    @Override
    public CommentShortDto updateCommentByUser(long userId, long comId, CommentUpdateUserRequest commentDto) {

        User author = getUserById(userId);
        Comment comment = getCommentById(comId);

        comment.setText(commentDto.getText());
        //после изменения пользователем своего комментария, комментарий подлежит модерации
        comment.setState(CommentState.PENDING);
        return CommentMapper.toCommentShortDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByUser(long userId, long comId) {

        User author = getUserById(userId);
        Comment comment = getCommentById(comId);

        if (!Objects.equals(comment.getAuthor().getId(), userId)) {
            throw new NoSuchCommentException("Comment with id=" + comId + " for user with id=" + userId + " not found.");
        }

        commentRepository.delete(comment);
    }

    @Override
    public Collection<CommentFullDto> getAllCommentsForAuthor(long userId, int from, int size) {
        User author = getUserById(userId);
        return commentRepository.getAllByAuthor(author, PageMapper.getPagable(from, size, "published")).stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CommentShortDto> getAllEventsCommentsForPublic(long eventId, int from, int size) {
        Event event = getEventById(eventId);
        checkThatEventPublished(event);

        return commentRepository.getAllByEventAndState(event,
                        CommentState.PUBLISHED,
                        PageMapper.getPagable(from, size, "published"))
                .stream()
                .map(CommentMapper::toCommentShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CommentFullDto> getAllCommentsEventByAdmin(long eventId, int from, int size) {
        Event event = getEventById(eventId);
        checkThatEventPublished(event);
        return commentRepository.getAllByEvent(event, PageMapper.getPagable(from, size, "published")).stream()
                .map(CommentMapper::toCommentFullDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentFullDto getCommentByIdForAdmin(long comId) {
        Comment comment = getCommentById(comId);
        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public CommentFullDto updateCommentByAdmin(long comId, CommentUpdateAdminRequest commentDto) {
        Comment comment = getCommentById(comId);

        if (comment.getState().equals(CommentState.PUBLISHED)) {
            throw new IllegalCommentStateException("Only unpublished comments can be updated");
        }

        if (commentDto.getText() != null) {
            comment.setText(commentDto.getText());
        }

        if (commentDto.getAction() != null) {
            if (commentDto.getAction().equals(CommentStateActionAdmin.PUBLISH_COMMENT)) {
                comment.setPublished(LocalDateTime.now().withNano(0));
                comment.setState(CommentState.PUBLISHED);
            } else if (commentDto.getAction().equals(CommentStateActionAdmin.REJECT_COMMENT)) {
                comment.setState(CommentState.REJECTED);
            } else {
                throw new IllegalCommentStateException("Unknown comment admin action");
            }
        }

        return CommentMapper.toCommentFullDto(comment);
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(long comId) {
        Comment comment = getCommentById(comId);
        commentRepository.delete(comment);
    }

    private void checkThatEventPublished(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new IllegalEventStateException("Only published events can be commented");
        }
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));
    }

    private Event getEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));
    }

    private Comment getCommentById(long comId) {
        return commentRepository.findById(comId)
                .orElseThrow(() -> new NoSuchCommentException("Comment with id=" + comId + " not found."));
    }
}
