package ru.practicum.ewm.service.event;

import ru.practicum.ewm.model.event.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto getById(Long id);

    List<EventShortDto> getAllShort(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size);

    List<EventFullDto> getAll(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto updateForUser(UpdateEventUserRequest eventDto, Long userId, Long eventId);

    EventFullDto updateForAdmin(Long id, UpdateEventAdminRequest eventDto);

    List<EventShortDto> getByUserId(Long id, int from, int size);

    EventFullDto create(NewEventDto newEventDto, Long id);

    EventFullDto getEventByUserId(Long userId, Long eventId);

}
