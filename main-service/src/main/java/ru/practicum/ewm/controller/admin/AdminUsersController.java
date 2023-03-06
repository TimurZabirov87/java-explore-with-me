package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/users")
public class AdminUsersController {
    private final UserService userService;

    //users
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserDto userDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/users, create: \n {}", userDto);
        return userService.create(userDto);
    }

    //Возвращает информацию обо всех пользователях (учитываются параметры ограничения выборки),
    // либо о конкретных (учитываются указанные идентификаторы)
    @GetMapping
    public List<UserDto> getAllUser(@RequestParam(value = "ids", required = false) Long[] users,
                                    @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                    @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Получен Get запрос к эндпоинту: /users \n users: {}, \n from = {} \n size = {}", users, from, size);
        return userService.getAll(users, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        log.info("Получен Delete запрос к эндпоинту: /users. Удаление user: {}", id);
        userService.delete(id);
    }

}
