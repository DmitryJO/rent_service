package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Параметры фильтрации каталога вещей")
public record ItemFilter(
        @Schema(description = "ID категории")
        Long categoryId,
        @Schema(description = "Минимальная цена за день")
        BigDecimal minPrice,
        @Schema(description = "Максимальная цена за день")
        BigDecimal maxPrice,
        @Schema(description = "Минимальное количество в наличии")
        Integer minQuantity,
        @Schema(description = "Только доступные для аренды (quantity > 0)")
        Boolean availableOnly
) {

}
