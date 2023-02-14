package ru.practicum.ewm.model.event.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.dto.UserShortDto;
import ru.practicum.ewm.model.user.entity.User;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {

    public static EventFullDto toEventFullDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {

        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                initiator,
                event.getLocation(),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );

    }


    public static EventShortDto toEventShortDto(Event event, CategoryDto categoryDto, UserShortDto initiator) {

        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getEventDate(),
                initiator,
                event.isPaid(),
                event.getTitle(),
                event.getViews());

    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {

        return new Event(
                null,
                newEventDto.getAnnotation(),
                category,
                0L,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                initiator,
                newEventDto.getLocation(),
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.isRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle(),
                0L
        );
    }

}
