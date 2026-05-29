package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(description = "Запрос на создание бронирования")
public record CreateOrderRequest(
        @NotNull
        @Schema(description = "ID вещи")
        Long itemId,
        @NotBlank
        @Size(max = 255)
        @Schema(description = "ФИО клиента")
        String customerFullName,
        @NotBlank
        @Size(max = 32)
        @Schema(description = "Телефон клиента")
        String customerPhone,
        @NotNull
        @Schema(description = "Дата начала аренды (включительно)")
        LocalDate startDate,
        @NotNull
        @Schema(description = "Дата окончания аренды (включительно)")
        LocalDate endDate
) {

}
