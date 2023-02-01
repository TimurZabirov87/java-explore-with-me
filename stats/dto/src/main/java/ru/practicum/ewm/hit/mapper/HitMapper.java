package ru.practicum.ewm.hit.mapper;

import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.hit.model.Hit;

public class HitMapper {

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getId() != null ? hit.getId() : null,
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getCreated()
        );
    }

    public static Hit toHit(HitDto hitDto) {
        return new Hit(
                hitDto.getId() != null ? hitDto.getId() : null,
                hitDto.getApp(),
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getCreated()
        );
    }

}
