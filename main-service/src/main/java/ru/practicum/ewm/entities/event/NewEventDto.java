package ru.practicum.ewm.entities.event;

import ru.practicum.ewm.entities.location.Location;

import java.time.LocalDateTime;

public class NewEventDto {
    private String annotation; //required min = 20, max = 2000
    private long category; //required
    private String description; //required min = 20, max 7000
    private LocalDateTime eventDate; //required
    private Location location; //required
    private boolean paid; //default = false
    private long participantLimit; //required, default = 0 (no limit)
    private boolean requestModeration; //required, default = true
    private String title; //required min = 3, max =120
}
