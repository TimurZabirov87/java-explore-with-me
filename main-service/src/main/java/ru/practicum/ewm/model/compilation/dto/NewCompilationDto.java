package ru.practicum.ewm.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    @Size(max = 128)
    private String title; //required
}
