package ru.dmsmirnov.rent.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;

@Schema(description = "Запрос на изменение статуса заказа")
public record UpdateOrderStatusRequest(
        @NotNull
        @Schema(description = "Новый статус заказа")
        RentalOrderStatus status
) {

}
