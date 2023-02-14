package ru.practicum.ewm.model.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCategoryDto {
    @NotBlank
    private String name; //required
}
