package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.participation.ConfirmedRequests;
import ru.practicum.ewm.model.participation.RequestStatus;
import ru.practicum.ewm.model.participation.entity.Request;
import ru.practicum.ewm.model.user.entity.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEvent(Event event);

    List<Request> findAllByRequester(User requester);

    List<Request> findAllByEventAndRequester(Event event, User requester);

    List<Request> findAllByEventAndStatus(Event event, RequestStatus status);

    @Query(" select r from Request r " +
            "where r.id in :ids ")
    List<Request> findAllByIdIn(List<Long> ids);

    Long countByEventAndStatus(Event event, RequestStatus status);

    @Query("select new ru.practicum.ewm.model.participation.ConfirmedRequests(r.event.id, count(r.status)) " +
            "from Request r " +
            "where r.status = 'CONFIRMED' " +
            "and r.event in :events " +
            "group by r.event.id ")
    List<ConfirmedRequests> getConfirmedRequestsByEvents(List<Event> events);
}
