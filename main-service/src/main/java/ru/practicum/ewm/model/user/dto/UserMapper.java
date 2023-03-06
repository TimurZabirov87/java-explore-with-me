package ru.practicum.ewm.model.user.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.user.entity.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserDto userToUserDto(User user) {
        if (user == null) {
            return null;
        }

        UserDto userDto = new UserDto();

        if (user.getId() != null) {
            userDto.setId(user.getId());
        }
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }


    public static User userDtoToUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        return user;
    }

    public static UserShortDto userToUserShortDto(User user) {
        if (user == null) {
            return null;
        }

        UserShortDto userShortDto = new UserShortDto();

        if (user.getId() != null) {
            userShortDto.setId(user.getId());
        }
        userShortDto.setName(user.getName());

        return userShortDto;
    }
}
