package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.model.category.dto.CategoryMapper;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.EventStateActionAdmin;
import ru.practicum.ewm.model.event.SortShortDtoByViewsComparator;
import ru.practicum.ewm.model.event.dto.*;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.participation.ConfirmedRequests;
import ru.practicum.ewm.model.participation.RequestStatus;
import ru.practicum.ewm.model.user.dto.UserMapper;
import ru.practicum.ewm.model.user.entity.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.PageMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    private final StatsClient statsClient;


    @Override
    public EventFullDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + id + " not found."));

        //?????????????? ???????????? ???????? ????????????????????????
        if (event.getPublishedOn() == null) {
            throw new NoSuchEventException("Event with id=" + id + " not found.");
        }

        return getEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllShort(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        Page<Event> events;
        Pageable pageable;
        if (!Objects.equals(sort, "EVENT_DATE") && !Objects.equals(sort, "VIEWS") && sort != null) {
           throw new IllegalEventSortingException("?????????????????????? ?????????? ???????????? ???? ???????? ?????? ???? ????????????????????.");
        }
        if (sort != null && Objects.equals(sort, "EVENT_DATE")) {
            pageable = PageRequest.of((from) % size, size, Sort.by("eventDate").descending());
        } else {
            pageable = PageMapper.getPagable(from, size);
        }

        if (onlyAvailable) {
            if (rangeStart == null || rangeEnd == null) {
                events = eventRepository.searchAllAvailablePublishedEvents(text, paid, LocalDateTime.now(), categories, pageable);
            } else {
                events = eventRepository.searchAllAvailablePublishedEventsWithDate(text, paid, rangeStart, rangeEnd, categories, pageable);
            }
        } else {
            if (rangeStart == null || rangeEnd == null) {
                events = eventRepository.searchAllPublishedEvents(text, paid, LocalDateTime.now(), categories, pageable);
            } else {
                events = eventRepository.searchAllPublishedEventsWithDate(text, paid, rangeStart, rangeEnd, categories, pageable);
            }
        }

        List<EventShortDto> result = getEventsShorts(events.stream().collect(Collectors.toList()));

        if (Objects.equals(sort, "VIEWS")) {
            return result.stream()
                    .sorted(new SortShortDtoByViewsComparator())
                    .collect(Collectors.toList());
        } else {
            return result;
        }

    }

    @Override
    public List<EventFullDto> getAll(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        Pageable pageable = PageMapper.getPagable(from, size);
        List<EventState> eventStates = new ArrayList<>();
        if (states != null) {
            for (String state : states) {
                eventStates.add(EventState.valueOf(state));
            }
        } else {
            eventStates = null;
        }
        Page<Event> events;
        if (rangeStart == null || rangeEnd == null) {
            events = eventRepository.searchAllEventsByUsers(users, eventStates, categories, LocalDateTime.now(), pageable);
        } else {
            events = eventRepository.searchAllEventsByUsersWithDate(users, eventStates, categories, rangeStart, rangeEnd, pageable);
        }

        return getEventsFulls(events.stream().collect(Collectors.toList()));
    }

    @Transactional
    @Override
    public EventFullDto updateForUser(UpdateEventUserRequest eventDto, Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NoSuchEventException("Event with id=" + eventId + " for user with id=" + userId + " not found.");
        }

        //???????????????? ?????????? ???????????? ???????????????????? ?????????????? ?????? ?????????????? ?? ?????????????????? ???????????????? ?????????????????? (?????????????????? ?????? ???????????? 409)
        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new IllegalEventStateException("Only pending or canceled events can be changed");
        }

        //???????? ?? ?????????? ???? ?????????????? ???????????????? ?????????????? ???? ?????????? ???????? ????????????, ?????? ?????????? ?????? ???????? ???? ???????????????? ?????????????? (?????????????????? ?????? ???????????? 409)
        if (eventDto.getEventDate() != null && eventDto.getEventDate().minusHours(2).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: ???????????? ?????????????????? ???????? ?? ?????????? ???? ????????????," +
                    " ?????? ?????????? ?????? ???????? ???? ???????????????? ??????????????. Value: " + eventDto.getEventDate());
        }

        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));
            event.setCategory(category);
        }
        if (eventDto.getAnnotation() != null && !event.getAnnotation().isBlank()) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getDescription() != null && !eventDto.getDescription().isBlank()) {
            event.setDescription(eventDto.getDescription());
        }

        updateEvent(event, eventDto.getEventDate(), eventDto.getLocation(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getRequestModeration(), eventDto);

        switch (eventDto.getStateAction()) {
            case CANCEL_REVIEW:
                event.setState(EventState.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                break;
            default:
            throw new IllegalEventStateException("Unknown event state");
        }

        if (eventDto.getTitle() != null && !eventDto.getTitle().isBlank()) {
            event.setTitle(eventDto.getTitle());
        }

        return getEventFullDto(event);

    }



    @Transactional
    @Override
    public EventFullDto updateForAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        //?????????????? ?????????? ??????????????????????, ???????????? ???????? ?????? ?? ?????????????????? ???????????????? ???????????????????? (?????????????????? ?????? ???????????? 409)
        if (!event.getState().equals(EventState.PENDING)) {
            throw new IllegalEventStateException("Only pending events can be changed");
        }

        //?????????????? ?????????? ??????????????????, ???????????? ???????? ?????? ?????? ???? ???????????????????????? (?????????????????? ?????? ???????????? 409)
        if (event.getPublishedOn() != null && eventDto.getStateAction().equals(EventStateActionAdmin.REJECT_EVENT)) {
            throw new IllegalEventStateException("Only unpublished events can be rejected");
        }

        if (eventDto.getEventDate() != null && eventDto.getEventDate().minusHours(1).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: ???????????? ?????????????????? ???????? ?? ?????????? ???? ????????????," +
                    " ?????? ?????????? ?????? ???? ???????????????? ??????????????. Value: " + eventDto.getEventDate());
        }

        if (eventDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));
            event.setCategory(category);
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        updateEvent(event, eventDto.getEventDate(), eventDto.getLocation(), eventDto.getPaid(), eventDto.getParticipantLimit(), eventDto.getRequestModeration(), eventDto);

        switch (eventDto.getStateAction()) {
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now().withNano(0));
                break;
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                break;
            default:
                throw new IllegalEventStateException("Illegal event state");
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        Event updatedEvent = eventRepository.save(event);

        return getEventFullDto(updatedEvent);


    }

    @Override
    public List<EventShortDto> getByUserId(Long userId, int from, int size) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Pageable pageable = PageMapper.getPagable(from, size);

        List<Event> events = eventRepository.findAllByInitiator(initiator, pageable).stream().collect(Collectors.toList());

        return getEventsShorts(events);

    }

    @Transactional
    @Override
    public EventFullDto create(NewEventDto eventDto, Long userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));


        //???????? ?? ?????????? ???? ?????????????? ???????????????? ?????????????? ???? ?????????? ???????? ????????????, ?????? ?????????? ?????? ???????? ???? ???????????????? ?????????????? (?????????????????? ?????? ???????????? 409)
        if (eventDto.getEventDate().minusHours(2).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: ???????????? ?????????????????? ???????? ?? ?????????? ???? ????????????," +
                    " ?????? ?????????? ?????? ???????? ???? ???????????????? ??????????????. Value: " + eventDto.getEventDate());
        }

        Event event = eventRepository.save(EventMapper.toEvent(eventDto, initiator, category));

        return EventMapper.toEventFullDto(event,
                                          CategoryMapper.toCategoryDto(category),
                                          UserMapper.userToUserShortDto(initiator),
                           0L,
                                    0);
    }

    @Override
    public EventFullDto getEventByUserId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NoSuchEventException("Event with id=" + eventId + " for user with id=" + userId + " not found.");
        }

        return getEventFullDto(event);
    }


    public Integer getEventsViews(Long eventId) {
        String[] uris = {"/events/" + eventId};

        ResponseEntity<Object> response = statsClient.getStats("2000-01-01 00:00:00",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris, false);


        List<LinkedHashMap<Object, Object>> stats = (List<LinkedHashMap<Object, Object>>) statsClient.getStats("2000-01-01 00:00:00",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                uris, false).getBody();
        if (stats != null && !stats.isEmpty()) {
            return (Integer) stats.get(0).get("hits");
        } else {
            return 0;
        }
    }

    public Map<Long, Integer> getEventsViewsMap(List<Long> eventsIds) {
        List<String> uris = new ArrayList<>();
        for (Long eventId: eventsIds) {
            uris.add("/events/" + eventId);
        }
        String[] urisArray = uris.toArray(new String[uris.size()]);

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

    private List<EventShortDto> getEventsShorts(List<Event> eventSet) {
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
                .collect(Collectors.toList());
    }

    private List<EventFullDto> getEventsFulls(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequests =
                requestRepository.getConfirmedRequestsByEvents(new ArrayList<>(events))
                        .stream().collect(Collectors.toMap(ConfirmedRequests::getEventId,
                                ConfirmedRequests::getConfirmedRequests));
        Map<Long, Integer> viewsMap = getEventsViewsMap(new ArrayList<>(eventIds));

        return events.stream()
                .map(event -> EventMapper.toEventFullDto(event,
                        CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.userToUserShortDto(event.getInitiator()),
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        viewsMap.get(event.getId())))
                .collect(Collectors.toList());

    }

    private EventFullDto getEventFullDto(Event event) {
        Long confirmedRequests = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);
        Integer views = getEventsViews(event.getId());

        return EventMapper.toEventFullDto(event,
                CategoryMapper.toCategoryDto(event.getCategory()),
                UserMapper.userToUserShortDto(event.getInitiator()),
                confirmedRequests,
                views);
    }

    private void updateEvent(Event event, LocalDateTime eventDate, Location location, Boolean paid, Long participantLimit, Boolean requestModeration, UpdateEvent eventDto) {

        if (eventDate != null) {
            event.setEventDate(eventDate);
        }

        if (location != null) {
            event.setLocation(location);
        }

        if (paid != null) {
            event.setPaid(paid);
        }

        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
    }

}
