package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;
import ru.practicum.ewm.service.category.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/admin/categories")
public class AdminCategoriesController {
    private final CategoryService categoryService;

    //categories
    //имя категории должно быть уникальным
    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@RequestBody @Valid CategoryDto categoryDto,
                                      @PathVariable Long catId) {
        log.info("Получен Patch запрос к эндпоинту: /admin/categories, update catId{}: \n {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    //имя категории должно быть уникальным
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Получен Post запрос к эндпоинту: /admin/categories create: \n {}", newCategoryDto);
        return categoryService.create(newCategoryDto);
    }

    //с категорией не должно быть связано ни одного события
    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Получен Delete запрос к эндпоинту: /admin/categories. Удаление категории: {}", catId);
        categoryService.delete(catId);
    }

}
