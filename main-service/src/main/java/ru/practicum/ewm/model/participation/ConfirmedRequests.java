package ru.practicum.ewm.model.participation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ConfirmedRequests {
    private Long eventId;
    private Long confirmedRequests;
}
