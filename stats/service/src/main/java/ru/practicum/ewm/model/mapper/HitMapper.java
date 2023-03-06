package ru.practicum.ewm.model.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.model.App;
import ru.practicum.ewm.model.Hit;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {

    public static HitDto toHitDto(Hit hit) {
        return new HitDto(
                hit.getId(),
                hit.getApp().getName(),
                hit.getUri(),
                hit.getIp(),
                hit.getCreated()
        );
    }

    public static Hit toHit(HitDto hitDto, App app) {
        return new Hit(
                hitDto.getId(),
                app,
                hitDto.getUri(),
                hitDto.getIp(),
                hitDto.getCreated()
        );
    }

}
