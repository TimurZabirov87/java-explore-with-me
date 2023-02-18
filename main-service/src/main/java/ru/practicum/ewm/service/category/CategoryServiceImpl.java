package ru.practicum.ewm.service.category;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.CategoryIsNotEmptyException;
import ru.practicum.ewm.exceptions.NoSuchCategoryException;
import ru.practicum.ewm.model.category.dto.CategoryDto;
import ru.practicum.ewm.model.category.dto.CategoryMapper;
import ru.practicum.ewm.model.category.dto.NewCategoryDto;
import ru.practicum.ewm.model.category.entity.Category;
import ru.practicum.ewm.model.event.entity.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.PageMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CategoryDto> getAll(int from, int size) {

        Pageable pageable = PageMapper.getPagable(from, size);

        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());

    }

    @Override
    public CategoryDto getById(long id) {
        return CategoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + id + " not found.")));
    }

    @Transactional
    @Override
    public CategoryDto update(long catId, CategoryDto categoryDto) {

        Category categoryToUpdate = categoryRepository.findById(catId)
                .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + catId + " not found."));

        categoryToUpdate.setName(categoryDto.getName());

        return CategoryMapper.toCategoryDto(categoryToUpdate);
    }

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchCategoryException("Category with id=" + id + " not found."));
        List<Event> eventsList = eventRepository.findByCategory(category);

        if (eventsList.size() > 0) {
            throw new CategoryIsNotEmptyException("Category is not empty");
        }

        categoryRepository.delete(category);
    }
}
