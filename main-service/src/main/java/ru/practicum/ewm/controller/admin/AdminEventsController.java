package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.service.event.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/admin/events")
public class AdminEventsController {
    private final EventService eventService;


    //events
    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                           @RequestParam(value = "states", required = false) List<String> states,
                                           @RequestParam(value = "categories", required = false) List<Long> categories,
                                           @RequestParam(value = "rangeStart", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(value = "rangeEnd", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                           @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /admin/events \n users id = {} \n states = {} " +
                        "\n categories id = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
    //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
    //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
    @PatchMapping("/{eventId}")
    public EventFullDto publishEvent(@PathVariable Long eventId,
                                     @RequestBody UpdateEventAdminRequest eventDto) {
        log.info("Получен Patch запрос publish к эндпоинту: /admin/events event id = {}, \n {}", eventId, eventDto);
        return eventService.updateForAdmin(eventId, eventDto);
    }

}
