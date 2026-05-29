package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;

@Schema(description = "Запрос на создание вещи")
public record CreateItemRequest(
        @NotNull
        @Schema(description = "ID категории")
        Long categoryId,
        @NotBlank
        @Size(max = 255)
        @Schema(description = "Название")
        String name,
        @Schema(description = "Описание")
        String description,
        @Schema(description = "Состояние (по умолчанию GOOD)")
        ItemCondition condition,
        @NotNull
        @Positive
        @Schema(description = "Цена за день")
        BigDecimal pricePerDay,
        @NotNull
        @Min(0)
        @Schema(description = "Количество единиц в парке")
        Integer quantity,
        @Schema(description = "URL изображений")
        List<String> imageUrls
) {

}
