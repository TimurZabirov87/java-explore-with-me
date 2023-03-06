package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/events")
public class PublicEventsController {
    private final EventService eventService;

    private final StatsClient statClient;

    @Value("${app.name}")
    private String app;

    //Events
    //событие должно быть опубликовано
    //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    @GetMapping("/{id}")
    public EventFullDto getEvents(@PathVariable Long id,
                                      HttpServletRequest request) {
        log.info("Получен Get запрос к эндпоинту: /events, id = {}", id);
        statClient.createHit(new HitDto(app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));

        return eventService.getById(id);
    }

    //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
    //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
    //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "paid", defaultValue = "false") boolean paid,
                                            @RequestParam(value = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(value = "sort", required = false) String sort,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                            @Positive @RequestParam(value = "size", defaultValue = "10") int size,
                                            HttpServletRequest request) {
        log.info("Получен Get запрос к эндпоинту: /events");
        statClient.createHit(new HitDto(app,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));
        return eventService.getAllShort(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

}
