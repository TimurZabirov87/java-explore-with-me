package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.event.dto.EventFullDto;
import ru.practicum.ewm.model.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.service.category.CategoryService;
import ru.practicum.ewm.service.compilation.CompilationService;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.user.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin")
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    //events
    @GetMapping("/events")
    public List<EventFullDto> getAllEvents(@RequestParam(value = "users", required = false) List<Long> users,
                                           @RequestParam(value = "states", required = false) List<String> states,
                                           @RequestParam(value = "categories", required = false) List<Long> categories,
                                           @RequestParam(value = "rangeStart", required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                           @RequestParam(value = "rangeEnd", required = false)
                                       @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                           @NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                           @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /admin/events \n users id = {} \n states = {} " +
                        "\n categories id = {}, rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getAll(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
    //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
    //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
    @PatchMapping("/events/{eventId}")
    public EventFullDto publishEvent(@PathVariable(required = true) Long eventId,
                                     @RequestBody UpdateEventAdminRequest eventDto) {
        log.info("Получен Patch запрос publish к эндпоинту: /admin/events event id = {}, \n {}", eventId, eventDto);
        return eventService.updateForAdmin(eventId, eventDto);
    }

    //categories
    //имя категории должно быть уникальным
    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                      @PathVariable(required = true) Long catId) {
        log.info("Получен Patch запрос к эндпоинту: /admin/categories, update catId{}: \n {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    //имя категории должно быть уникальным
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/categories create: \n {}", newCategoryDto);
        return categoryService.create(newCategoryDto);
    }

    //с категорией не должно быть связано ни одного события
    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(required = true) Long catId) {
        log.info("Получен Delete запрос к эндпоинту: /admin/categories. Удаление категории: {}", catId);
        categoryService.delete(catId);
    }

    //users
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/users, create: \n {}", userDto);
        return userService.create(userDto);
    }

    //Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
    // либо о конкретных (учитываются указанные идентификаторы)
    @GetMapping("/users")
    public List<UserDto> getAllUser(@RequestParam(value = "ids", required = false) Long[] users,
                                    @NumberFormat @RequestParam(value = "from", defaultValue = "0") int from,
                                    @NumberFormat @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /users \n users: {}, \n from = {} \n size = {}", users, from, size);
        return userService.getAll(users, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable(required = true) Integer id) {
        log.info("Получен Delete запрос к эндпоинту: /users. Удаление user: {}", id);
        userService.delete(id);
    }

    //compilations
    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/compilations \n, {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable(required = true) long compId) {
        log.info("Получен Delete запрос к эндпоинту: /compilation. Удаление Compilation:{}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto addEventToCompilation(@PathVariable(required = true) long compId,
                                      @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен patch запрос к эндпоинту: /compilation/{compId}.compilation id:{}, \n:{}", compId, updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

}
