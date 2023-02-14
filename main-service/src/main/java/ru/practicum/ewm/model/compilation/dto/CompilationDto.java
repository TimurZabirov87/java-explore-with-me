package ru.practicum.ewm.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;
    private long id; //required
    private boolean pinned; //required
    private String title; //required
}
