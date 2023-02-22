package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.comment.entity.Comment;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.entity.User;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    Page<Comment> getAllByEvent(Event event, Pageable pageable);

    Page<Comment> getAllByEventAndState(Event event, CommentState state, Pageable pageable);

    Page<Comment> getAllByAuthor(User author, Pageable pageable);
}
