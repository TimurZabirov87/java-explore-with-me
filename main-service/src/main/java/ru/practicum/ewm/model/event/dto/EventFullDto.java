package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFullDto {
    private long id;
    private String annotation; //required
    private CategoryDto category; //required
    private long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate; //required
    private UserShortDto initiator; //required
    private Location location; //required
    private Boolean paid; //required
    private long participantLimit; //required, default = 0 (no limit)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    private Boolean requestModeration; //required, default = true
    private EventState state;
    private String title; //required
    private Integer views;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventFullDto that = (EventFullDto) o;
        return id == that.id && Objects.equals(annotation, that.annotation) &&
                Objects.equals(createdOn, that.createdOn) &&
                Objects.equals(description, that.description) &&
                Objects.equals(eventDate, that.eventDate) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, annotation, createdOn, description, eventDate, title);
    }
}
