package ru.practicum.ewm.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    @Email
    private String email; // required, @email
    private long id;
    @NotBlank
    private String name; //required
}
