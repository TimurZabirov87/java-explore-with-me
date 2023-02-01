package ru.practicum.ewm.service;

import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.stats.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatService {
    HitDto create(HitDto hitDto);

    List<Stats> getStats(LocalDateTime startRange, LocalDateTime endRange, Optional<List<String>> uris, boolean unique);
}
