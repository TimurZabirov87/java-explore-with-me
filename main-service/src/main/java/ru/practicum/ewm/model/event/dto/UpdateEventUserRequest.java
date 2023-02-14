package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.event.EventStateActionUser;
import ru.practicum.ewm.model.location.Location;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateEventUserRequest {

    private String annotation; //min = 20, max = 2000
    private Long category;
    private String description; // min = 20, max 7000
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration;
    private EventStateActionUser stateAction;
    private String title; //required min = 3, max =120

}
