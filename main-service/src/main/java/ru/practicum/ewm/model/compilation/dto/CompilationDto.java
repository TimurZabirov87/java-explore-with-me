package ru.practicum.ewm.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.event.dto.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationDto {
    private Set<EventShortDto> events;
    private Long id; //required
    private Boolean pinned; //required
    private String title; //required
}
