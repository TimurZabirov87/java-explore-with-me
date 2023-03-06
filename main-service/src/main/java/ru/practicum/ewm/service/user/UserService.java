package ru.practicum.ewm.service.user;

import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.model.user.dto.UserShortDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    List<UserDto> getAll(Long[] users, int from, int size);

    UserDto getUserById(long id);

    UserShortDto getUserShortById(long id);

    void delete(long id);
}
