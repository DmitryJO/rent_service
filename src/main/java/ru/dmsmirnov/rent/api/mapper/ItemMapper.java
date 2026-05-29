package ru.dmsmirnov.rent.api.mapper;

import ru.dmsmirnov.rent.api.dto.ItemResponse;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemResponse toResponse(ItemEntity entity) {
        return new ItemResponse(
                entity.getId(),
                entity.getCategory().getId(),
                entity.getCategory().getName(),
                entity.getName(),
                entity.getDescription(),
                entity.getCondition(),
                entity.getPricePerDay(),
                entity.getQuantity(),
                entity.getImageUrls()
        );
    }

}
