package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.service.category.CategoryService;
import ru.practicum.ewm.service.compilation.CompilationService;
import ru.practicum.ewm.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class PublicController {

    private final EventService eventService;
    private final CompilationService compilationService;
    private final CategoryService categoryService;

    private final StatsClient statClient;

    //Events

    //событие должно быть опубликовано
    //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    @GetMapping("/events/{id}")
    public EventFullDto getEvents(@PathVariable(required = true) Long id,
                                      HttpServletRequest request) {
        log.info("Получен Get запрос к эндпоинту: /events, id = {}", id);
        statClient.createHit(new HitDto("ewm-server",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));

        EventFullDto eventDto = eventService.getById(id, request.getRequestURI());

        return eventDto;
    }

    //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
    //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
    //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    @GetMapping("/events")
    public List<EventShortDto> getAllEvents(@RequestParam(value = "text", required = false) String text,
                                            @RequestParam(value = "categories", required = false) List<Long> categories,
                                            @RequestParam(value = "paid", defaultValue = "false") boolean paid,
                                            @RequestParam(value = "rangeStart", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(value = "rangeEnd", required = false)
                                            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @RequestParam(value = "onlyAvailable", defaultValue = "false") boolean onlyAvailable,
                                            @RequestParam(value = "sort", required = false) String sort,
                                            @NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                            @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size,
                                            HttpServletRequest request) {
        log.info("Получен Get запрос к эндпоинту: /events");
        statClient.createHit(new HitDto("ewm-server",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()));
        return eventService.getAllShort(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    //Compilations
    @GetMapping("/compilations")
    public List<CompilationDto> getAllCompilations(@RequestParam(value = "pinned", defaultValue = "false") boolean pinned,
                                                   @NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                                   @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /compilations, pinned = {}, from = {}, size = {}", pinned, from, size);
        return compilationService.getAll(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilation(@PathVariable(required = true) long compId) {
        log.info("Получен Get запрос к эндпоинту: /compilations/, compId = " + compId);
        return compilationService.getById(compId);
    }

    //Categories

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                           @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /categories, from = {}, size = {}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategory(@PathVariable(required = true) long catId) {
        log.info("Получен Get запрос к эндпоинту: /categories, catId = " + catId);
        return categoryService.getById(catId);
    }
}
