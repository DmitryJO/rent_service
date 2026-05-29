package ru.dmsmirnov.rent.domain.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.CreateCategoryRequest;
import ru.dmsmirnov.rent.domain.exception.CategoryNotFoundException;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryEntity> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<CategoryEntity> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public CategoryEntity createCategory(CreateCategoryRequest request) {
        CategoryEntity category = new CategoryEntity();
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    @Transactional
    public CategoryEntity create(CategoryEntity category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public CategoryEntity update(CategoryEntity category) {
        if (!categoryRepository.existsById(category.getId())) {
            throw new CategoryNotFoundException(category.getId());
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

}
