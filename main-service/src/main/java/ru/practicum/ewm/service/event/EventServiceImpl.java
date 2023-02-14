package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.CategoryMapper;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.EventStateActionAdmin;
import ru.practicum.ewm.model.event.SortShortDtoByViewsComparator;
import ru.practicum.ewm.model.event.dto.*;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.dto.UserMapper;
import ru.practicum.ewm.model.user.dto.UserShortDto;
import ru.practicum.ewm.model.user.entity.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.PageMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private final StatsClient statsClient;

    @Override
    public EventFullDto getById(Long id, String requestUri) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + id + " not found."));

        //событие должно быть опубликовано
        if (event.getPublishedOn() == null) {
            throw new NoSuchEventException("Event with id=" + id + " not found.");
        }
        UserShortDto initiator = UserMapper.userToUserShortDto(event.getInitiator());
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(event.getCategory());
        event.setViews(event.getViews() + 1);
        eventRepository.save(event);

        return EventMapper.toEventFullDto(event, categoryDto, initiator);
    }

    @Override
    public List<EventShortDto> getAllShort(String text, List<Long> categories, boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, String sort, int from, int size) {
        Page<Event> events;
        Pageable pageable;
        if (!Objects.equals(sort, "EVENT_DATE") && !Objects.equals(sort, "VIEWS") && sort != null) {
           throw new IllegalEventSortingException("Сортировать можно только по дате или по просмотрам.");
        }
        if (sort != null && Objects.equals(sort, "EVENT_DATE")) {
            pageable = PageRequest.of((from) % size, size, Sort.by("eventDate").descending());;
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

        List<Event> viewsPlus1List = events.stream().collect(Collectors.toList());
        for (Event event : viewsPlus1List) {
            event.setViews(event.getViews() + 1);
        }
        eventRepository.saveAll(viewsPlus1List);

        List<EventShortDto> result = viewsPlus1List.stream()
                .map(event -> EventMapper.toEventShortDto(event,
                        CategoryMapper.toCategoryDto(event.getCategory()),
                        UserMapper.userToUserShortDto(event.getInitiator())))
                .collect(Collectors.toList());

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

        List<EventFullDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(EventMapper.toEventFullDto(event,
                    CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.userToUserShortDto(event.getInitiator())));
        }

        return result;
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

        //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
        if (!event.getState().equals(EventState.PENDING) && !event.getState().equals(EventState.CANCELED)) {
            throw new IllegalEventStateException("Only pending or canceled events can be changed");
        }

        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
        if (eventDto.getEventDate() != null && eventDto.getEventDate().minusHours(2).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: должно содержать дату и время не раньше," +
                    " чем через два часа от текущего момента. Value: " + eventDto.getEventDate());
        }

        Category category;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));

        } else {
            category = event.getCategory();
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

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

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

        Event savedEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(savedEvent,
                                          CategoryMapper.toCategoryDto(category),
                                          UserMapper.userToUserShortDto(user));

    }

    @Transactional
    @Override
    public EventFullDto updateForAdmin(Long eventId, UpdateEventAdminRequest eventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
        if (!event.getState().equals(EventState.PENDING)) {
            throw new IllegalEventStateException("Only pending events can be changed");
        }

        //событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
        if (event.getPublishedOn() != null && eventDto.getStateAction().equals(EventStateActionAdmin.REJECT_EVENT)) {
            throw new IllegalEventStateException("Only unpublished events can be rejected");
        }

        if (eventDto.getEventDate() != null && eventDto.getEventDate().minusHours(1).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: должно содержать дату и время не раньше," +
                    " чем через час от текущего момента. Value: " + eventDto.getEventDate());
        }

        Category category;
        if (eventDto.getCategory() != null) {
            category = categoryRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));

        } else {
            category = event.getCategory();
        }

        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(category);
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(eventDto.getEventDate());
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(eventDto.getLocation());
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

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

        return EventMapper.toEventFullDto(updatedEvent,
                CategoryMapper.toCategoryDto(category),
                UserMapper.userToUserShortDto(event.getInitiator()));


    }

    @Override
    public List<EventShortDto> getByUserId(Long userId, int from, int size) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Pageable pageable = PageMapper.getPagable(from, size);

        List<Event> events = eventRepository.findAllByInitiator(initiator, pageable).stream().collect(Collectors.toList());

        List<EventShortDto> result = new ArrayList<>();

        for (Event event : events) {
            result.add(EventMapper.toEventShortDto(event,
                    CategoryMapper.toCategoryDto(event.getCategory()),
                    UserMapper.userToUserShortDto(event.getInitiator())));
        }

        return result;
    }

    @Transactional
    @Override
    public EventFullDto create(NewEventDto eventDto, Long userId) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + eventDto.getCategory() + " not found."));


        //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
        if (eventDto.getEventDate().minusHours(2).minusSeconds(1).isBefore(LocalDateTime.now())) {
            throw new IllegalEventDateTimeException("Field: eventDate. Error: должно содержать дату и время не раньше," +
                    " чем через два часа от текущего момента. Value: " + eventDto.getEventDate());
        }

        Event event = eventRepository.save(EventMapper.toEvent(eventDto, initiator, category));

        return EventMapper.toEventFullDto(event,
                                          CategoryMapper.toCategoryDto(category),
                                          UserMapper.userToUserShortDto(initiator));
    }

    @Override
    public EventFullDto getEventByUserId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            throw new NoSuchEventException("Event with id=" + eventId + " for user with id=" + userId + " not found.");
        }

        return EventMapper.toEventFullDto(event,
                                          CategoryMapper.toCategoryDto(event.getCategory()),
                                          UserMapper.userToUserShortDto(event.getInitiator()));
    }


}
