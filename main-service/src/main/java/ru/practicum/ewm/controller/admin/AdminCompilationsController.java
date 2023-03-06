package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.compilation.dto.CompilationDto;
import ru.practicum.ewm.model.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.model.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationsController {
    private final CompilationService compilationService;

    //compilations
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/compilations \n, {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable long compId) {
        log.info("Получен Delete запрос к эндпоинту: /compilation. Удаление Compilation:{}", compId);
        compilationService.delete(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto addEventToCompilation(@PathVariable long compId,
                                      @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("Получен patch запрос к эндпоинту: /compilation/{compId}.compilation id:{}, \n:{}", compId, updateCompilationRequest);
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

}
