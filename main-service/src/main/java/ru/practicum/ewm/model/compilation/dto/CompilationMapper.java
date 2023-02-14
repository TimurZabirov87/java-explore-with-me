package ru.practicum.ewm.model.compilation.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.category.dto.CategoryMapper;
import ru.practicum.ewm.model.compilation.entity.Compilation;
import ru.practicum.ewm.model.event.dto.EventMapper;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        List<EventShortDto> shorts = compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(event,
                                                          CategoryMapper.toCategoryDto(event.getCategory()),
                                                          UserMapper.userToUserShortDto(event.getInitiator())))
                .collect(Collectors.toList());

        CompilationDto compilationDto = new CompilationDto();

        compilationDto.setId(compilation.getId());
        compilationDto.setEvents(shorts);
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());

        return compilationDto;
    }


    public static Compilation toCompilation(CompilationDto compilationDto, List<Event> eventsList) {
        if (compilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();

        compilation.setId(compilationDto.getId());
        compilation.setEvents(eventsList);
        compilation.setPinned(compilationDto.isPinned());
        compilation.setTitle(compilationDto.getTitle());

        return compilation;
    }

    public static Compilation toCompilation(NewCompilationDto compilationDto, List<Event> eventsList) {
        if (compilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();

        compilation.setId(null);
        compilation.setEvents(eventsList);
        compilation.setPinned(compilationDto.isPinned());
        compilation.setTitle(compilationDto.getTitle());

        return compilation;
    }



}
