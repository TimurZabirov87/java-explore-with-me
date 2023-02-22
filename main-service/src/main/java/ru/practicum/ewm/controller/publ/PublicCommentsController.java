package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/events")
public class PublicCommentsController {

    private final CommentService commentService;

    /**
     Метод позволяет получить все опубликованные комментарии к событию, с возможностью пагинации.
     Событие должно быть опубликовано.
     Публичный эндпойнт.
     */
    @GetMapping("{eventId}/comments")
    public Collection<CommentShortDto> getAllEventsCommentsForPublic(
            @PathVariable long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос к эндпоинту: /events/{eventId}/comments, eventId = {}, from = {}, size = {}", eventId, from, size);
        return commentService.getAllEventsCommentsForPublic(eventId, from, size);
    }
}
