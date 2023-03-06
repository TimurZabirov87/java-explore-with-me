package ru.practicum.ewm.model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    @NotEmpty
    @Email
    private String email; // required, @email
    private long id;
    @NotBlank
    private String name; //required
}
