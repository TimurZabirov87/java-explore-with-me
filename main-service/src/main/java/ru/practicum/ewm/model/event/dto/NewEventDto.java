package ru.practicum.ewm.model.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.model.location.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class NewEventDto {
    @NotBlank
    private String annotation; //required min = 20, max = 2000
    @NotNull
    private long category; //required
    @NotBlank
    private String description; //required min = 20, max 7000
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime eventDate; //required
    @NotNull
    private Location location; //required
    private boolean paid; //default = false
    private long participantLimit; //required, default = 0 (no limit)
    private boolean requestModeration; //required, default = true
    @NotBlank
    private String title; //required min = 3, max =120
}
