package ru.practicum.ewm.entities.event;

import ru.practicum.ewm.entities.category.CategoryDto;
import ru.practicum.ewm.entities.user.UserShortDto;

import java.time.LocalDateTime;

public class EventShortDto {
    private String annotation; //required
    private CategoryDto category; //required
    private int confirmedRequests;
    private LocalDateTime eventDate; //required
    private long id;
    private UserShortDto initiator; //required
    private boolean paid; //required
    private String title; //required
    private long views;
}
