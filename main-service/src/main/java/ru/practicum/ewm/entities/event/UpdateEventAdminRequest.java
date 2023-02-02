package ru.practicum.ewm.entities.event;

import ru.practicum.ewm.entities.location.Location;

import java.time.LocalDateTime;

public class UpdateEventAdminRequest {

    private String annotation; //min = 20, max = 2000
    private long category;
    private String description; // min = 20, max 7000
    private LocalDateTime eventDate;
    private Location location;
    private boolean paid;
    private long participantLimit;
    private boolean requestModeration;
    private EventStateActionAdmin stateAction;
    private String title; //required min = 3, max =120

}
