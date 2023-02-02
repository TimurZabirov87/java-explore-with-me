package ru.practicum.ewm.entities.compilation;

import java.util.Set;

public class UpdateCompilationRequest {
    private Set<Long> events;
    private boolean pinned;
    private String title;
}
