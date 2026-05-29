package ru.dmsmirnov.rent.api.mapper;

import ru.dmsmirnov.rent.api.dto.CategoryResponse;
import ru.dmsmirnov.rent.api.dto.ItemResponse;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryResponse toResponse(CategoryEntity entity) {
        return new CategoryResponse(entity.getId(), entity.getName(), entity.getDescription());
    }

}
