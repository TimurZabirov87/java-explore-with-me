package ru.practicum.ewm.service.category;

import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(long catID);

    CategoryDto update(long catID, CategoryDto categoryDto);

    CategoryDto create(NewCategoryDto newCategoryDto);

    void delete(Long catID);
}
