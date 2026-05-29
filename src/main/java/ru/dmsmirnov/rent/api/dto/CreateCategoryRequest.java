package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на создание категории")
public record CreateCategoryRequest(
        @NotBlank
        @Size(max = 128)
        @Schema(description = "Название категории")
        String name,
        @Schema(description = "Описание")
        String description
) {

}
