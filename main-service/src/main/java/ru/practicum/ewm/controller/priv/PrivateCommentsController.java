package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.dto.CommentFullDto;
import ru.practicum.ewm.model.comment.dto.CommentShortDto;
import ru.practicum.ewm.model.comment.dto.CommentUpdateUserRequest;
import ru.practicum.ewm.model.comment.dto.NewCommentDto;
import ru.practicum.ewm.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/users")
public class PrivateCommentsController {

    private final CommentService commentService;

    /**
    Метод позволяет пользователю получить список всех опубликованных комментариев к событию с возможностью пагинации.
     Событие должно быть опубликовано.
     */
    @GetMapping("/{userId}/events/{eventId}/comments")
    public Collection<CommentShortDto> getAllCommentsForEvents(
            @PathVariable long userId,
            @PathVariable long eventId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос к эндпоинту: /users/{userId}/events/{eventId}/comments, id = {}, eventId = {}, from = {}, size = {}", userId, eventId, from, size);
        return commentService.getAllEventsComments(userId, eventId, from, size);
    }

    /**
     Метод позволяет зарегистрированному пользователю получить список всех своих комментариев с возможностью пагинации.
     */
    @GetMapping("/{userId}/comments")
    public Collection<CommentFullDto> getAllCommentsForEventInitiator(
            @PathVariable long userId,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(name = "size", defaultValue = "10") @Positive int size) {
        log.info("Получен GET запрос к эндпоинту: /users/{userId}/comments, id = {},, from = {}, size = {}", userId, from, size);
        return commentService.getAllCommentsForAuthor(userId, from, size);
    }

    /**
    Метод позволяет получить автору полную версию комментария по его id.
     */
    @GetMapping("/{userId}/comments/{comId}")
    public CommentFullDto getCommentByIdForUser(
            @PathVariable long userId,
            @PathVariable long comId) {
        log.info("Получен GET запрос к эндпоинту: /users/{userId}//comments/{comId}, id = {}, comId = {}", userId, comId);
        return commentService.getCommentByIdForAuthor(userId, comId);
    }

    /**
     Метод позволяет оставить комментарий к событию (по id события).
     Комментарии могут оставлять только зарегистрированные пользователи.
     Событие должно быть опубликовано.
     Участие в событии при этом не требуется.
     Инициатор события также может оставить комментарий к нему.
     */
    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentShortDto createComment(
            @PathVariable long userId,
            @RequestParam(name = "eventId") @Positive long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен POST запрос к эндпоинту: /users/{userId}/comments, id = {}, eventId = {}, \n comment = {}", userId, eventId, newCommentDto);
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    /**
     Метод позволяет автору комментария изменить его текст.
     */
    @PatchMapping("/{userId}/comments/{comId}")
    public CommentShortDto updateCommentByUser(
            @PathVariable long userId,
            @PathVariable long comId,
            @Valid @RequestBody CommentUpdateUserRequest commentDto) {
        log.info("Получен PATCH запрос к эндпоинту: /users/{userId}/comments/{comId}, id = {}, comId = {}, \n comment = {}", userId, comId, commentDto);

        return commentService.updateCommentByUser(userId, comId, commentDto);
    }

    /**
     Метод позволяет автору комментария удалить его.
     Комментарий можно удалить, как до модерации, так и после.
     */
    @DeleteMapping("/{userId}/comments/{comId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(
            @PathVariable long userId,
            @PathVariable long comId) {
        log.info("Получен DELETE запрос к эндпоинту: /users/{userId}/comments/{comId}, id = {}, comId = {}", userId, comId);
        commentService.deleteCommentByUser(userId, comId);
    }

}