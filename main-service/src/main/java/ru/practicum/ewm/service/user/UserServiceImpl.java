package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.NoSuchUserException;
import ru.practicum.ewm.model.user.dto.UserDto;
import ru.practicum.ewm.model.user.dto.UserMapper;
import ru.practicum.ewm.model.user.dto.UserShortDto;
import ru.practicum.ewm.model.user.entity.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.PageMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.userToUserDto(userRepository.save(UserMapper.userDtoToUser(userDto)));
    }

    @Override
    public List<UserDto> getAll(Long[] users, int from, int size) {
        if (users != null && users.length > 0) {
            List<Long> ids = List.of(users);
            Pageable pageable = PageMapper.getPagable(from, size);
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::userToUserDto)
                    .collect(Collectors.toList());
        } else {
            Pageable pageable = PageMapper.getPagable(from, size);
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::userToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public UserDto getUserById(long id) {

        return UserMapper.userToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + id + " not found.")));
    }

    @Override
    public UserShortDto getUserShortById(long id) {

        return UserMapper.userToUserShortDto(userRepository.findById(id)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + id + " not found.")));
    }

    @Transactional
    @Override
    public void delete(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));
        userRepository.delete(user);
    }
}
