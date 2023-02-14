package ru.practicum.ewm.model.event;

import ru.practicum.ewm.model.event.dto.EventShortDto;

import java.util.Comparator;

public class SortShortDtoByViewsComparator implements Comparator<EventShortDto> {

    @Override
    public int compare(EventShortDto e1, EventShortDto e2) {
        return (int) (e2.getViews() - e1.getViews());
    }

    @Override
    public Comparator<EventShortDto> reversed() {
        return Comparator.super.reversed();
    }
}
