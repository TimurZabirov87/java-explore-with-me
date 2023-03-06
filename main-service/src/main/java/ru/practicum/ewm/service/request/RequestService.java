package ru.practicum.ewm.service.request;

import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.participation.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getEventRequests(Long id, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long id, Long eventId, EventRequestStatusUpdateRequest request);

    List<ParticipationRequestDto> getByUserId(Long userId);

    ParticipationRequestDto create(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId);
}
