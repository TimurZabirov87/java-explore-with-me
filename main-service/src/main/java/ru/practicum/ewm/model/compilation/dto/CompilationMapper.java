package ru.practicum.ewm.model.compilation.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.compilation.entity.Compilation;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.entity.Event;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Set<EventShortDto> eventShortDtoList) {
        if (compilation == null) {
            return null;
        }

        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(eventShortDtoList);
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }


    public static Compilation toCompilation(CompilationDto compilationDto, Set<Event> eventsList) {
        if (compilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();

        compilation.setId(compilationDto.getId());
        compilation.setEvents(eventsList);
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());

        return compilation;
    }

    public static Compilation toCompilation(NewCompilationDto compilationDto, Set<Event> eventsList) {
        if (compilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();

        compilation.setId(null);
        compilation.setEvents(eventsList);
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());

        return compilation;
    }



}
