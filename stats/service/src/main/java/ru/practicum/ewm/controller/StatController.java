package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.service.StatService;
import ru.practicum.ewm.stats.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> create(@RequestBody HitDto hitDto) {
        log.info("Получен Post запрос к эндпоинту: /hit");
        return new ResponseEntity<>(statService.create(hitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Stats>> getStats(@RequestParam(value = "start")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startRange,
                                                @RequestParam(value = "end")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endRange,
                                                @RequestParam(value = "uris") Optional<List<String>> uris,
                                                @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Получен Get запрос к эндпоинту: /stats");
        return new ResponseEntity<>(statService.getStats(startRange, endRange, uris, unique), HttpStatus.OK);
    }
}
