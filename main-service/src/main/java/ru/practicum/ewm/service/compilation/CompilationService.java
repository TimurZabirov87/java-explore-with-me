package ru.practicum.ewm.service.compilation;

import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getAll(boolean pinned, int from, int size);

    CompilationDto getById(long compilationId);

    CompilationDto create(NewCompilationDto compilationDto);

    void delete(long compilationId);

    CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest updateCompilationRequest);

}
