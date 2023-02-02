package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.stats.Stats;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.ewm.stats.Stats(app.name, h.uri, count(h)) " +
            "from Hit h " +
            "join App app on app = h.app " +
            "where h.uri in (:uris) and h.created between :startDate and :endDate " +
            "group by h.uri, app.name " +
            "order by count(h) desc")
    List<Stats> getStatByUris(@Param("uris") List<String> uris,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

    @Query("select new ru.practicum.ewm.stats.Stats(app.name, h.uri, count(h.ip)) " +
            "from Hit h " +
            "join App app on app = h.app " +
            "where h.uri in (:uris) and h.created between :startDate and :endDate " +
            "group by h.uri, app.name " +
            "order by count(h.ip) desc")
    List<Stats> getUniqueStatByUris(@Param("uris") List<String> uris,
                              @Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("select new ru.practicum.ewm.stats.Stats(app.name, h.uri, count(h)) " +
            "from Hit h " +
            "join App app on app = h.app " +
            "where h.created between :startDate and :endDate " +
            "group by h.uri, h.app " +
            "order by count(h) desc")
    List<Stats> getAllStat(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate);

    @Query("select new ru.practicum.ewm.stats.Stats(app.name, h.uri, count(h.ip)) " +
            "from Hit h " +
            "join App app on app = h.app " +
            "where h.created between :startDate and :endDate " +
            "group by h.ip, h.uri, h.app " +
            "order by count(h.ip) desc")
    List<Stats> getAllUniqueStat(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

}
