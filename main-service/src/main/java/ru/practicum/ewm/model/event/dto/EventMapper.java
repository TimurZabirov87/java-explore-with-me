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

    public static EventFullDto toEventFullDto(Event event,
                                              CategoryDto categoryDto,
                                              UserShortDto initiator,
                                              Long confirmedRequests,
                                              Integer views) {

        return new EventFullDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                initiator,
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views
        );

    }


    public static EventShortDto toEventShortDto(Event event,
                                                CategoryDto categoryDto,
                                                UserShortDto initiator,
                                                Long confirmedRequests,
                                                Integer views) {

        return new EventShortDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                confirmedRequests,
                event.getEventDate(),
                initiator,
                event.getPaid(),
                event.getTitle(),
                views);

    }

    public static Event toEvent(NewEventDto newEventDto, User initiator, Category category) {

        return new Event(
                null,
                newEventDto.getAnnotation(),
                category,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                initiator,
                newEventDto.getLocation(),
                newEventDto.getPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.getRequestModeration(),
                EventState.PENDING,
                newEventDto.getTitle()
        );
    }

}
