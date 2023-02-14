package ru.practicum.ewm.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCompilationDto {
    @NotNull
    private List<Long> events;
    @NotNull
    private boolean pinned;
    @NotBlank
    private String title; //required
}
