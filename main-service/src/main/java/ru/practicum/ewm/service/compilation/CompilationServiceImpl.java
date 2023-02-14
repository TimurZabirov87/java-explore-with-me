package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.NoSuchCompilationException;
import ru.practicum.ewm.exceptions.NoSuchEventException;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.CompilationMapper;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.compilation.entity.Compilation;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.PageMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {

        Pageable pageable = PageMapper.getPagable(from, size);

        return compilationRepository.getAllByPinned(pinned, pageable).stream()
                .map(CompilationMapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(long id) {
        return CompilationMapper.toCompilationDto(compilationRepository.findById(id)
                .orElseThrow(() -> new NoSuchCompilationException("Compilation with id=" + id + " not found.")));
    }

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto compilationDto) {

        List<Long> eventIds = compilationDto.getEvents();
        List<Event> eventList = eventRepository.findAllById(eventIds);
        if (eventIds.size() == eventList.size()) {
            Compilation compilationTosave = CompilationMapper.toCompilation(compilationDto, eventList);
            return CompilationMapper.toCompilationDto(compilationRepository.save(compilationTosave));
        } else {
            List<Long> foundedEventsIds = eventList.stream().map(Event::getId).collect(Collectors.toList());
            List<Long> notFoundedEventsIds = new ArrayList<>(eventIds);
            notFoundedEventsIds.removeAll(foundedEventsIds);
            throw new NoSuchEventException("Event with ids=" + notFoundedEventsIds + " not found.");
        }
    }

    @Transactional
    @Override
    public void delete(long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NoSuchCompilationException("Compilation with id=" + compilationId + " not found."));
        compilationRepository.delete(compilation);
    }

    @Transactional
    @Override
    public CompilationDto updateCompilation(long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationToUpdate = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NoSuchCompilationException("Compilation with id=" + compilationId + " not found."));
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> eventList = eventRepository.findAllById(updateCompilationRequest.getEvents());
            if (updateCompilationRequest.getEvents().size() == eventList.size()) {
                compilationToUpdate.setEvents(eventList);
            } else {
                List<Long> foundedEventsIds = eventList.stream().map(Event::getId).collect(Collectors.toList());
                List<Long> notFoundedEventsIds = new ArrayList<>(updateCompilationRequest.getEvents());
                notFoundedEventsIds.removeAll(foundedEventsIds);
                throw new NoSuchEventException("Event with ids=" + notFoundedEventsIds + " not found.");
            }

        }
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilationToUpdate.setTitle(updateCompilationRequest.getTitle());
        }
        compilationToUpdate.setPinned(updateCompilationRequest.isPinned());
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToUpdate));
    }

}
