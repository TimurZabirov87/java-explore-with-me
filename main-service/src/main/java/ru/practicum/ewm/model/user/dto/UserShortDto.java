package ru.practicum.ewm.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserShortDto {
    private long id; //required
    private String name; //required
}
