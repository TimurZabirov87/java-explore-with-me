package ru.practicum.ewm.model.category.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {
    private long id; //readonly
    @NotBlank
    private String name; //required

}
