package ru.dmsmirnov.rent.api.dto;

import java.math.BigDecimal;
import java.util.List;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;

public record ItemResponse(
        Long id,
        Long categoryId,
        String categoryName,
        String name,
        String description,
        ItemCondition condition,
        BigDecimal pricePerDay,
        Integer quantity,
        List<String> imageUrls
) {

}
