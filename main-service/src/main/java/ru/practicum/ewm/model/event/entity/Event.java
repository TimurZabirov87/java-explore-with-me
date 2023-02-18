package ru.practicum.ewm.model.event.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.entity.User;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ANNOTATION", nullable = false)
    @Size(min = 20, max = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "DESCRIPTION", nullable = false)
    @Size(min = 20, max = 7000)
    private String description;
    @Column(name = "EVENT_DATE", nullable = false)
    private LocalDateTime eventDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;
    @Embedded
    private Location location;
    @Column(name = "PAID", nullable = false)
    private Boolean paid;
    @Column(name = "PARTICIPANT_LIMIT", nullable = false)
    private long participantLimit;
    @Column(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;
    @Column(name = "REQUEST_MODERATION", nullable = false)
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private EventState state;
    @Column(name = "TITLE", nullable = false)
    @Size(min = 1, max = 128)
    private String title;
}
