package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.CreateOrderRequest;
import ru.dmsmirnov.rent.api.dto.OrderResponse;
import ru.dmsmirnov.rent.api.mapper.OrderMapper;
import ru.dmsmirnov.rent.domain.service.RentalOrderService;

@Tag(name = "Orders", description = "Бронирование вещей")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final RentalOrderService rentalOrderService;

    @Operation(summary = "Создать бронирование")
    @ApiResponse(responseCode = "201", description = "Заказ создан")
    @ApiResponse(responseCode = "409", description = "Нет доступности на выбранный период")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return OrderMapper.toResponse(rentalOrderService.createOrder(request));
    }

}
