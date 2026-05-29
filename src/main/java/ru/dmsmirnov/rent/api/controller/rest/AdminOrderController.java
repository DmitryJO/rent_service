package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.OrderResponse;
import ru.dmsmirnov.rent.api.dto.UpdateOrderStatusRequest;
import ru.dmsmirnov.rent.api.mapper.OrderMapper;
import ru.dmsmirnov.rent.domain.exception.OrderNotFoundException;
import ru.dmsmirnov.rent.domain.service.RentalOrderService;

@Tag(name = "Admin Orders", description = "Администрирование заказов")
@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final RentalOrderService rentalOrderService;

    @Operation(summary = "Список всех заказов")
    @ApiResponse(responseCode = "200", description = "Список заказов")
    @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    @GetMapping
    public List<OrderResponse> getOrders() {
        return rentalOrderService.findAll().stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Получить заказ по ID")
    @ApiResponse(responseCode = "200", description = "Заказ найден")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return OrderMapper.toResponse(rentalOrderService.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @Operation(summary = "Изменить статус заказа")
    @ApiResponse(responseCode = "200", description = "Статус обновлён")
    @PatchMapping("/{id}/status")
    public OrderResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return OrderMapper.toResponse(rentalOrderService.updateStatus(id, request.status()));
    }

    @Operation(summary = "Отменить заказ")
    @ApiResponse(responseCode = "200", description = "Заказ отменён")
    @PostMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id) {
        return OrderMapper.toResponse(rentalOrderService.cancelOrder(id));
    }

}
