package ru.practicum.ewm.service.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.exceptions.NoSuchCompilationException;
import ru.practicum.ewm.exceptions.NoSuchEventException;
import ru.practicum.ewm.model.category.dto.CategoryMapper;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.CompilationMapper;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.model.compilation.entity.Compilation;
import ru.practicum.ewm.model.event.dto.EventMapper;
import ru.practicum.ewm.model.event.dto.EventShortDto;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.participation.ConfirmedRequests;
import ru.practicum.ewm.model.user.dto.UserMapper;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.service.PageMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<CompilationDto> getAll(boolean pinned, int from, int size) {

        Pageable pageable = PageMapper.getPagable(from, size);
        List<Compilation> compilations = compilationRepository.getAllByPinned(pinned, pageable).stream().collect(Collectors.toList());

        List<CompilationDto> result = new ArrayList<>();

        for (Compilation compilation : compilations) {
            Set<EventShortDto> eventShortDtos = new HashSet<>();

            if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
                Set<Event> eventSet = compilation.getEvents();
                eventShortDtos = getEventsShorts(eventSet);
            }
            result.add(CompilationMapper.toCompilationDto(compilation, eventShortDtos));
        }

        return result;
    }

    @Override
    public CompilationDto getById(long id) {
        Compilation compilation = compilationRepository.findById(id)
                .orElseThrow(() -> new NoSuchCompilationException("Compilation with id=" + id + " not found."));

        Set<EventShortDto> eventShortDtos = new HashSet<>();

        if (compilation.getEvents() != null && !compilation.getEvents().isEmpty()) {
            Set<Event> eventSet = compilation.getEvents();
            eventShortDtos = getEventsShorts(eventSet);
        }

        return CompilationMapper.toCompilationDto(compilation, eventShortDtos);

    }

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto compilationDto) {

        Set<Long> eventIds = compilationDto.getEvents();
        Set<Event> eventSet = new HashSet<>();
        Set<EventShortDto> eventShortDtos = new HashSet<>();

        if (eventIds.size() > 0) {
            eventSet = new HashSet<>(eventRepository.findAllById(eventIds));
            if (eventIds.size() == eventSet.size()) {
                eventShortDtos = getEventsShorts(eventSet);
            } else {
                List<Long> foundedEventsIds = eventSet.stream().map(Event::getId).collect(Collectors.toList());
                List<Long> notFoundedEventsIds = new ArrayList<>(eventIds);
                notFoundedEventsIds.removeAll(foundedEventsIds);
                throw new NoSuchEventException("Event with ids=" + notFoundedEventsIds + " not found.");
            }
        }

        Compilation compilationToSave = CompilationMapper.toCompilation(compilationDto, eventSet);
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToSave), eventShortDtos);

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
        Set<Event> eventSet;
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            eventSet = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            if (updateCompilationRequest.getEvents().size() == eventSet.size()) {
                compilationToUpdate.setEvents(eventSet);
            } else {
                List<Long> foundedEventsIds = eventSet.stream().map(Event::getId).collect(Collectors.toList());
                List<Long> notFoundedEventsIds = new ArrayList<>(updateCompilationRequest.getEvents());
                notFoundedEventsIds.removeAll(foundedEventsIds);
                throw new NoSuchEventException("Event with ids=" + notFoundedEventsIds + " not found.");
            }
        }
        eventSet = compilationToUpdate.getEvents();
        if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
            compilationToUpdate.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilationToUpdate.setPinned(updateCompilationRequest.getPinned());
        }

        Set<EventShortDto> eventShortDtos = getEventsShorts(eventSet);

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilationToUpdate), eventShortDtos);
    }


    public Map<Long, Integer> getEventsViewsMap(List<Long> eventsIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId: eventsIds) {
            uris.add("/events/" + eventId);
        }
        String[] urisArray = uris.toArray(new String[uris.size()]);

        ResponseEntity<Object> response = statsClient.getStats("2000-01-01 00:00:00",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                urisArray, false);

        List<LinkedHashMap<Object, Object>> stats = (List<LinkedHashMap<Object, Object>>) statsClient.getStats("2000-01-01 00:00:00",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                urisArray, false).getBody();

        Map<Long, Integer> eventViewsMap = new HashMap<>();
        if (stats != null && !stats.isEmpty()) {
            for (LinkedHashMap map : stats) {
                String uri = (String) map.get("uri");
                String[] urisAsArr = uri.split("/");
                Long id = Long.parseLong(urisAsArr[urisAsArr.length - 1]);
                eventViewsMap.put(id, (Integer) map.get("hits"));
            }
        }
        for (Long id : eventsIds) {
            if (!eventViewsMap.containsKey(id)) {
                eventViewsMap.put(id, 0);
            }
        }

        return eventViewsMap;
    }

    private Set<EventShortDto> getEventsShorts(Set<Event> eventSet) {
        List<Long> eventIds = eventSet.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests =
                requestRepository.getConfirmedRequestsByEvents(new ArrayList<>(eventSet))
                        .stream().collect(Collectors.toMap(ConfirmedRequests::getEventId,
                                ConfirmedRequests::getConfirmedRequests));
        Map<Long, Integer> viewsMap = getEventsViewsMap(new ArrayList<>(eventIds));

        return eventSet.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.userToUserShortDto(event.getInitiator()),
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewsMap.get(event.getId())))
                .collect(Collectors.toSet());
    }

}
