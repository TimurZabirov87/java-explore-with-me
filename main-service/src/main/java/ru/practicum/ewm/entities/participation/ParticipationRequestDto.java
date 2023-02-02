package ru.practicum.ewm.entities.participation;

import java.time.LocalDateTime;

public class ParticipationRequestDto {
    private LocalDateTime created;
    private long event;
    private long id;
    private long requester;
    private EventRequestStatus status;
}
