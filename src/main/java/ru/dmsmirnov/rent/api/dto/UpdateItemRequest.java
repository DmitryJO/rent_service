package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;

@Schema(description = "Запрос на обновление вещи (передавайте только изменяемые поля)")
public record UpdateItemRequest(
        @Schema(description = "ID категории")
        Long categoryId,
        @Size(max = 255)
        @Schema(description = "Название")
        String name,
        @Schema(description = "Описание")
        String description,
        @Schema(description = "Состояние")
        ItemCondition condition,
        @Positive
        @Schema(description = "Цена за день")
        BigDecimal pricePerDay,
        @Min(0)
        @Schema(description = "Количество единиц в парке")
        Integer quantity,
        @Schema(description = "URL изображений")
        List<String> imageUrls
) {

}
