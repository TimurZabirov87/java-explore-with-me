package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.hit.dto.HitDto;
import ru.practicum.ewm.service.StatService;
import ru.practicum.ewm.stats.Stats;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
@Validated
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<HitDto> create(@Valid @RequestBody HitDto hitDto) {
        log.info("Получен Post запрос к эндпоинту: /hit" + "\n" + hitDto);
        return new ResponseEntity<>(statService.create(hitDto), HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<Stats>> getStats(@RequestParam(value = "start")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startRange,
                                                @RequestParam(value = "end")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endRange,
                                                @RequestParam(value = "uris") Optional<List<String>> uris,
                                                @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        log.info("Получен Get запрос к эндпоинту: /stats" +
                "\n" + "startRange = " + startRange +
                "\n" + "endRange = " + endRange +
                "\n" + "uris = " + uris +
                "\n" + "unique = " + unique);
        return new ResponseEntity<>(statService.getStats(startRange, endRange, uris, unique), HttpStatus.OK);
    }
}
