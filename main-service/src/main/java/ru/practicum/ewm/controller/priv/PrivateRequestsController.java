package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.service.request.RequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class PrivateRequestsController {

    private final RequestService requestService;

    //requests
    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getUserEvents(@PathVariable Long userId) {
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
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam(value = "eventId") long eventId) {
        log.info("Получен post запрос к эндпоинту: /users/request, user id = {}, event id = {}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    //отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelByUser(@PathVariable Long userId,
                                                @PathVariable Long requestId) {
        log.info("Получен Patch запрос к эндпоинту: /users/request, user id = {}, requestId = {}", userId, requestId);
        return requestService.cancelRequestByUser(userId, requestId);
    }
}
