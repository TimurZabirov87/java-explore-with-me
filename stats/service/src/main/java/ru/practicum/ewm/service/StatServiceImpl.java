package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.mapper.HitMapper;
import ru.practicum.ewm.repository.AppRepository;
import ru.practicum.ewm.repository.StatRepository;
import ru.practicum.ewm.stats.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final AppRepository appRepository;

    @Override
    @Transactional
    public HitDto create(HitDto hitDto) {
        if (hitDto.getCreated() == null) {
            hitDto.setCreated(LocalDateTime.now());
        }
        App app = appRepository.findByName(hitDto.getApp());
        if (app == null) {
            app = appRepository.save(new App(hitDto.getApp()));
        }
        return HitMapper.toHitDto(statRepository.save(HitMapper.toHit(hitDto, app)));
    }

    @Override
    public List<Stats> getStats(LocalDateTime start, LocalDateTime end, Optional<List<String>> uris, boolean unique) {
        if (uris.isPresent() && !uris.get().isEmpty()) {
            if (unique) {
                return statRepository.getUniqueStatByUris(uris.get(), start, end);
            } else {
                return statRepository.getStatByUris(uris.get(), start, end);
            }
        } else {
            if (unique) {
                return statRepository.getAllUniqueStat(start, end);
            } else {
                return statRepository.getAllStat(start, end);
            }
        }
    }
}
