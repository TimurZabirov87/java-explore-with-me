package ru.practicum.ewm.entities.compilation;

import ru.practicum.ewm.entities.event.EventShortDto;

import java.util.Set;

public class CompilationDto {
    private Set<EventShortDto> events;
    private long id; //required
    private boolean pinned; //required
    private String title; //required
}
