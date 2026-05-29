package ru.dmsmirnov.rent.api.mapper;

import ru.dmsmirnov.rent.api.dto.OrderResponse;
import ru.dmsmirnov.rent.infrastructure.store.entity.RentalOrderEntity;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(RentalOrderEntity entity) {
        return new OrderResponse(
                entity.getId(),
                entity.getItem().getId(),
                entity.getCustomerFullName(),
                entity.getCustomerPhone(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus()
        );
    }

}
