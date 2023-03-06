package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.EventState;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.model.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByCategory(Category category);

    Page<Event> findAllByInitiator(User initiator, Pageable pageable);

    @Query(" SELECT e.id, " +
            "e.annotation, " +
            "e.category, " +
            "e.createdOn, " +
            "e.description, " +
            "e.eventDate, " +
            "e.initiator, " +
            "e.location, " +
            "e.paid, " +
            "e.participantLimit, " +
            "e.publishedOn, " +
            "e.requestModeration, " +
            "e.state, " +
            "e.title " +
            "FROM Event e " +
            "JOIN Request r on e.id = r.event.id " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in (:categories) or :categories is null)" +
            "and e.paid = :paid or :paid = false " +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and e.state = 'PUBLISHED'" +
            "and r.status = 'CONFIRMED'" +
            "GROUP BY e.id, e.annotation, e.category, e.createdOn, e.description, e.eventDate, e.initiator, e.location, e.paid, e.participantLimit, e.publishedOn, e.requestModeration, e.state, e.title " +
            "HAVING count(r.status) < e.participantLimit ")
    Page<Event> searchAllAvailablePublishedEventsWithDate(
            @Param("text") String text,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            @Param("categories") List<Long> categories,
            Pageable pageable);

    @Query(" select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in (:categories) or :categories is null)" +
            "and e.paid = :paid or :paid = false " +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and e.state = 'PUBLISHED'")
    Page<Event> searchAllPublishedEventsWithDate(
                                                @Param("text") String text,
                                                @Param("paid") Boolean paid,
                                                @Param("rangeStart") LocalDateTime rangeStart,
                                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                                @Param("categories") List<Long> categories,
                                                Pageable pageable);

    @Query(" SELECT e.id, " +
            "e.annotation, " +
            "e.category, " +
            "e.createdOn, " +
            "e.description, " +
            "e.eventDate, " +
            "e.initiator, " +
            "e.location, " +
            "e.paid, " +
            "e.participantLimit, " +
            "e.publishedOn, " +
            "e.requestModeration, " +
            "e.state, " +
            "e.title " +
            "FROM Event e " +
            "JOIN Request r on e.id = r.event.id " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in (:categories) or :categories is null)" +
            "and e.paid = :paid or :paid = false " +
            "and e.eventDate >= :now " +
            "and e.state = 'PUBLISHED'" +
            "and r.status = 'CONFIRMED'" +
            "GROUP BY e.id, e.annotation, e.category, e.createdOn, e.description, e.eventDate, e.initiator, e.location, e.paid, e.participantLimit, e.publishedOn, e.requestModeration, e.state, e.title " +
            "HAVING count(r.status) < e.participantLimit ")
    Page<Event> searchAllAvailablePublishedEvents(
                                                @Param("text") String text,
                                                @Param("paid") Boolean paid,
                                                @Param("now") LocalDateTime now,
                                                @Param("categories") List<Long> categories,
                                                Pageable pageable);

    @Query(" select e from Event e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) " +
            "or :text is null) " +
            "and (e.category.id in (:categories) or :categories is null) " +
            "and e.paid = :paid or :paid = false " +
            "and e.eventDate >= :now " +
            "and e.state = 'PUBLISHED'")
    Page<Event> searchAllPublishedEvents(
                                        @Param("text") String text,
                                        @Param("paid") Boolean paid,
                                        @Param("now") LocalDateTime now,
                                        @Param("categories") List<Long> categories,
                                        Pageable pageable);

    //List<Long> usersIds, List<String> states, List<Long> categoriesIds, LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size
    @Query("Select e from Event e " +
            "where (e.initiator.id in :users) " +
            "and (e.state in :state or :state is null) " +
            "and (e.category.id in (:category) or :category is null) " +
            "and e.eventDate between :rangeStart and :rangeEnd")
    Page<Event> searchAllEventsByUsersWithDate(@Param("users") List<Long> users,
                                         @Param("state") List<EventState> state,
                                         @Param("category") List<Long> category,
                                         @Param("rangeStart") LocalDateTime rangeStart,
                                         @Param("rangeEnd") LocalDateTime rangeEnd,
                                         Pageable pageable);

    @Query("Select e from Event e " +
            "where (e.initiator.id in :users) " +
            "and (e.state in :state or :state is null) " +
            "and (e.category.id in (:category) or :category is null) " +
            "and e.eventDate >= :now ")
    Page<Event> searchAllEventsByUsers(@Param("users") List<Long> users,
                                 @Param("state") List<EventState> state,
                                 @Param("category") List<Long> category,
                                 @Param("now") LocalDateTime now,
                                 Pageable pageable);


}
