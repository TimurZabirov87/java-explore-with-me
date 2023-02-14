package ru.practicum.ewm.model.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateCompilationRequest {
    private Set<Long> events;
    private boolean pinned;
    private String title;
}
