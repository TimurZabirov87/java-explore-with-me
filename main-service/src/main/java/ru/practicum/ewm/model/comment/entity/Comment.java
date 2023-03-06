package ru.practicum.ewm.model.comment.entity;

import lombok.*;
import ru.practicum.ewm.model.comment.CommentState;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column (name = "text", nullable = false)
    @Size(min = 20, max = 2000)
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "event_id", nullable = false)
    @NotNull
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "author_id", nullable = false)
    @NotNull
    private User author;
    @Column (name = "created", nullable = false)
    private LocalDateTime created;
    @Enumerated(EnumType.STRING)
    private CommentState state;
    private LocalDateTime published;

}
