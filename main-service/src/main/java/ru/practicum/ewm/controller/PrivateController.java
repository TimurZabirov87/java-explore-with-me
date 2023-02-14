package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.NumberFormat;
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
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class PrivateController {

    private final EventService eventService;
    private final RequestService requestService;

    //events
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getUserEvents(@PathVariable(required = true) Long userId,
                                             @NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                             @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /users/{userId}/events, user id = {}, from = {}, size = {}", userId, from, size);
        return eventService.getByUserId(userId, from, size);
    }

    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Valid NewEventDto newEventDto,
                                    @PathVariable(required = true) Long userId) {
        log.info("Получен post запрос к эндпоинту: /users/{userId}/events user id = {}, \n {}", userId, newEventDto);
        return eventService.create(newEventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getUserEvents(@PathVariable(required = true) Long userId,
                                      @PathVariable(required = true) Long eventId) {
        log.info("Получен Get запрос к эндпоинту: /users/{userId}/events/{eventId}, user id = {}, event id = {}",  userId, eventId);
        return eventService.getEventByUserId(userId, eventId);
    }

    //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@RequestBody UpdateEventUserRequest eventDto,
                                    @PathVariable(required = true) Long userId,
                                    @PathVariable(required = true) Long eventId) {
        log.info("Получен patch запрос к эндпоинту: /users/{userId}/events/{eventId} user id = {}, event id = {}, \n {}", userId, eventId, eventDto);
        return eventService.updateForUser(eventDto, userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable(required = true) Long userId,
                                                          @PathVariable(required = true) Long eventId) {
        log.info("Получен Get запрос к эндпоинту: /users/id/events/eventId/requests, user id = {}, event id = {}",  userId, eventId);
        return requestService.getEventRequests(userId, eventId);
    }

    //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
    //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
    //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
    //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequest(@PathVariable(required = true) Long userId,
                                                        @PathVariable(required = true) Long eventId,
                                                        @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Получен Patch запрос confirm к эндпоинту: /users/events, id = {}, eventId = {}, \n request = {}", userId, eventId, request);
        return requestService.updateRequest(userId, eventId, request);
    }

    //requests
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserEvents(@PathVariable(required = true) Long userId) {
        log.info("Получен Get запрос к эндпоинту: /users/request, user id = {}", userId);
        return requestService.getByUserId(userId);
    }

    //нельзя добавить повторный запрос (Ожидается код ошибки 409)
    //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
    //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
    //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
    //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable(required = true) Long userId,
                                                 @RequestParam(value = "eventId", required = true) long eventId) {
        log.info("Получен post запрос к эндпоинту: /users/request, user id = {}, event id = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    //отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelByUser(@PathVariable(required = true) Long userId,
                                                @PathVariable(required = true) Long requestId) {
        log.info("Получен Patch запрос к эндпоинту: /users/request, user id = {}, requestId = {}", userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }
}
