package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.dto.NewEventDto;
import ru.practicum.ewm.model.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.request.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class PrivateEventsController {

    private final EventService eventService;
    private final RequestService requestService;

    //events
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                             @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /users/{userId}/events, user id = {}, from = {}, size = {}", userId, from, size);
        return eventService.getByUserId(userId, from, size);
    }

    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        log.info("Получен post запрос к эндпоинту: /users/{userId}/events user id = {}, \n {}", userId, newEventDto);
        return eventService.create(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvents(@PathVariable Long userId,
                                      @PathVariable Long eventId) {
        log.info("Получен Get запрос к эндпоинту: /users/{userId}/events/{eventId}, user id = {}, event id = {}",  userId, eventId);
        return eventService.getEventByUserId(userId, eventId);
    }

    //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventUserRequest eventDto,
                                    @PathVariable Long userId,
                                    @PathVariable Long eventId) {
        log.info("Получен patch запрос к эндпоинту: /users/{userId}/events/{eventId} user id = {}, event id = {}, \n {}", userId, eventId, eventDto);
        return eventService.updateForUser(eventDto, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        log.info("Получен Get запрос к эндпоинту: /users/id/events/eventId/requests, user id = {}, event id = {}",  userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
    //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
    //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
    //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable Long userId,
                                                        @PathVariable Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Получен Patch запрос confirm к эндпоинту: /users/events, id = {}, eventId = {}, \n request = {}", userId, eventId, request);
        return requestService.updateRequest(userId, eventId, request);
    }

}
