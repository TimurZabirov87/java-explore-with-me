package ru.practicum.ewm.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Stats {
    String app;
    String uri;
    Long hits;
}
