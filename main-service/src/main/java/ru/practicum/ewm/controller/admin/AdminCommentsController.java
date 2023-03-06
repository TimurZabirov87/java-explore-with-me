package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.model.comment.dto.CommentUpdateAdminRequest;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin")
public class AdminCommentsController {

    private final CommentService commentService;

    /**
     Метод позволяет получить список всех опубликованных комментариев к событию с возможностью пагинации.
     Событие должно быть опубликовано.
     */
    @GetMapping("/events/{eventId}/comments")
    public Collection<CommentFullDto> getAllEventsCommentsForAdmin(
            @PathVariable long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос к эндпоинту: /admin/events/{eventId}/comments, id = {}, from = {}, size = {}",eventId, from, size);
        return commentService.getAllCommentsEventByAdmin(eventId, from, size);
    }

    /**
     Метод позволяет получить комментарий по его id.
     */
    @GetMapping("/comments/{comId}")
    public CommentFullDto getCommentByIdForAdmin(
            @PathVariable long comId) {
        log.info("Получен GET запрос к эндпоинту: /admin/comments/{comId}, id = {}",comId);
        return commentService.getCommentByIdForAdmin(comId);
    }

    /**
     Метод позволяет опубликовать или отклонить комментарий, также при необходимости отредактировать текст
     Комментарий не должен быть опубликован.
     */
    @PatchMapping("/comments/{comId}")
    public CommentFullDto updateCommentByAdmin(
            @PathVariable long comId,
            @Valid @RequestBody CommentUpdateAdminRequest commentDto) {
        log.info("Получен PATCH запрос к эндпоинту: /admin/comments/{comId}, id = {}",comId);
        return commentService.updateCommentByAdmin(comId, commentDto);
    }

    /**
     Метод позволяет удалить комментарий
     */
    @DeleteMapping("/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(
            @PathVariable long comId) {
        log.info("Получен DELETE запрос к эндпоинту: /admin/comments/{comId}, id = {}",comId);
        commentService.deleteCommentByAdmin(comId);
    }
}
