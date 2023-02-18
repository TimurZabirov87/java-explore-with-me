package ru.practicum.ewm.service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.*;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.participation.RequestStatus;
import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.participation.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.participation.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.participation.dto.RequestMapper;
import ru.practicum.ewm.model.participation.entity.Request;
import ru.practicum.ewm.model.user.entity.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        if (!Objects.equals(event.getInitiator().getId(), initiator.getId())) {
            throw new NoSuchEventException("Event with id=" + eventId + "for user with id=" + userId + " not found.");
        }

        return requestRepository.findByEvent(event).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request) {

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        List<Request> requests = requestRepository.findAllByIdIn(request.getRequestIds());
        Set<ParticipationRequestDto> confirmed = new HashSet<>();
        Set<ParticipationRequestDto> rejected = new HashSet<>();
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult(confirmed, rejected);
        List<Request> pendingRequests = requests.stream()
                .filter(request1 -> request1.getStatus().equals(RequestStatus.PENDING))
                .collect(Collectors.toList());
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        if (requests.size() > pendingRequests.size()) {
            throw new NoPendingRequestConfirmingException("Request must have status PENDING");
        }

        Long confirmedRequests = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request request1 : requests) {
                request1.setStatus(RequestStatus.CONFIRMED);
            }
            result.getConfirmedRequests().addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);

            return result;
        }

        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        if (event.getParticipantLimit() != 0 &&
                ((confirmedRequests + request.getRequestIds().size()) > event.getParticipantLimit()) &&
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new LimitOfRequestsReachedException("The participant limit has been reached");
        }

        //если при подтверждении данной заявки, лимит заявок для события исчерпан,
        // то все неподтверждённые заявки необходимо отклонить
        if (event.getParticipantLimit() != 0 &&
                ((confirmedRequests + request.getRequestIds().size()) == event.getParticipantLimit()) &&
                request.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (Request request1 : requests) {
                request1.setStatus(RequestStatus.CONFIRMED);
            }
            confirmed.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);

            List<Request> otherPendingRequests = requestRepository.findAllByEventAndStatus(event, RequestStatus.PENDING);
            for (Request request2 : otherPendingRequests) {
                request2.setStatus(RequestStatus.REJECTED);
            }
            requestRepository.saveAll(otherPendingRequests);
            rejected.addAll(otherPendingRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            result.setRejectedRequests(rejected);
            return result;
        }

        //во всех остальных случаях
        if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
            for (Request request1 : requests) {
                request1.setStatus(RequestStatus.CONFIRMED);
            }
            confirmed.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setConfirmedRequests(confirmed);
        } else if (request.getStatus().equals(RequestStatus.REJECTED)) {
            for (Request request1 : requests) {
                request1.setStatus(RequestStatus.REJECTED);
            }
            rejected.addAll(requests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
            requestRepository.saveAll(requests);
            result.setRejectedRequests(rejected);
        }

        return result;
    }

    @Override
    public List<ParticipationRequestDto> getByUserId(Long userId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        return requestRepository.findAllByRequester(requester).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NoSuchEventException("Event with id=" + eventId + " not found."));

        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (Objects.equals(requester.getId(), event.getInitiator().getId())) {
            throw new RequesterIsInitiatorException("Requester is initiator of event with id=" + eventId);
        }

        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getPublishedOn() == null) {
            throw new RequestForUnpublishedEventException("Event with id= " + eventId + " is unpublished");
        }

        Long confirmedRequests = requestRepository.countByEventAndStatus(event, RequestStatus.CONFIRMED);

        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        long requestCount = requestRepository.findByEvent(event).size();
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() == confirmedRequests) {
            throw new LimitOfRequestsReachedException("The participant limit has been reached");
        }
        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.findAllByEventAndRequester(event, requester).size() > 0) {
            throw new DoubleRequestException("Request already is exist");
        }
        Request request = new Request(null,
                LocalDateTime.now().withNano(0),
                event,
                requester,
                RequestStatus.PENDING);
        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        if (!event.getRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        return RequestMapper.toRequestDto(requestRepository.save(request));

    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequestByUser(Long userId, Long requestId) {

        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchUserException("User with id=" + userId + " not found."));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchParticipationRequestException("Request with id=" + requestId + " not found."));

        if (!Objects.equals(request.getRequester().getId(), requester.getId())) {
            throw new NoSuchParticipationRequestException("Request with id=" + requestId + " for user with id=" + userId + " not found.");
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
