package ru.practicum.ewm.entities.event;

import ru.practicum.ewm.entities.category.CategoryDto;
import ru.practicum.ewm.entities.location.Location;
import ru.practicum.ewm.entities.user.UserShortDto;

import java.time.LocalDateTime;

public class EventFullDto {
    private String annotation; //required
    private CategoryDto category; //required
    private int confirmedRequests;
    private LocalDateTime createdOn;
    private String description;
    private LocalDateTime eventDate; //required
    private long id;
    private UserShortDto initiator; //required
    private Location location; //required
    private boolean paid; //required
    private long participantLimit; //required, default = 0 (no limit)
    private LocalDateTime publishedOn;
    private boolean requestModeration; //required, default = true
    private EventState state;
    private String title; //required
    private long views;


}
