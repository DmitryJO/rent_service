package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.CreateItemRequest;
import ru.dmsmirnov.rent.api.dto.ItemResponse;
import ru.dmsmirnov.rent.api.dto.UpdateItemRequest;
import ru.dmsmirnov.rent.api.mapper.ItemMapper;
import ru.dmsmirnov.rent.domain.exception.ItemNotFoundException;
import ru.dmsmirnov.rent.domain.service.ItemService;

@Tag(name = "Admin Items", description = "Администрирование каталога вещей")
@RestController
@RequestMapping("/api/v1/admin/items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminItemController {

    private final ItemService itemService;

    @Operation(summary = "Список всех вещей")
    @GetMapping
    public List<ItemResponse> getItems() {
        return itemService.findAllWithCategory().stream()
                .map(ItemMapper::toResponse)
                .toList();
    }

    @Operation(summary = "Получить вещь по ID")
    @ApiResponse(responseCode = "404", description = "Вещь не найдена")
    @GetMapping("/{id}")
    public ItemResponse getItem(@PathVariable Long id) {
        return ItemMapper.toResponse(itemService.findByIdWithCategory(id)
                .orElseThrow(() -> new ItemNotFoundException(id)));
    }

    @Operation(summary = "Создать вещь")
    @ApiResponse(responseCode = "201", description = "Вещь создана")
    @ApiResponse(responseCode = "404", description = "Категория не найдена")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse createItem(@Valid @RequestBody CreateItemRequest request) {
        return ItemMapper.toResponse(itemService.createItem(request));
    }

    @Operation(summary = "Обновить вещь")
    @ApiResponse(responseCode = "404", description = "Вещь или категория не найдены")
    @PatchMapping("/{id}")
    public ItemResponse updateItem(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        return ItemMapper.toResponse(itemService.updateItem(id, request));
    }

    @Operation(summary = "Удалить вещь")
    @ApiResponse(responseCode = "204", description = "Вещь удалена")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }

}
