package ru.practicum.ewm.model.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class LocationDto {
    private double lat;
    private double lon;
}
