package ru.practicum.ewm.entities.participation;

import java.util.List;

public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private EventRequestStatus status;
}
