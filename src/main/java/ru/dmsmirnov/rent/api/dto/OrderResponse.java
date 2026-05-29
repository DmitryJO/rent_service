package ru.dmsmirnov.rent.api.dto;

import java.time.LocalDate;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;

public record OrderResponse(
        Long id,
        Long itemId,
        String customerFullName,
        String customerPhone,
        LocalDate startDate,
        LocalDate endDate,
        RentalOrderStatus status
) {

}
