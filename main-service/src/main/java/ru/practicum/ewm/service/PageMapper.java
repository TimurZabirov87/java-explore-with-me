package ru.practicum.ewm.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageMapper {
    public static Pageable getPagable(int from, int size, String sortColumn) {
        return PageRequest.of((from) % size, size, Sort.by(sortColumn).descending());
    }

    public static Pageable getPagable(int from, int size) {
        return getPagable(from, size, "id");
    }
}
