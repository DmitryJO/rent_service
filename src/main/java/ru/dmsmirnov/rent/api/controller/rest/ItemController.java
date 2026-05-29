package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.ItemFilter;
import ru.dmsmirnov.rent.api.dto.ItemResponse;
import ru.dmsmirnov.rent.api.mapper.ItemMapper;
import ru.dmsmirnov.rent.domain.service.ItemService;

@Tag(name = "Items", description = "Каталог арендуемых вещей")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(summary = "Список вещей с фильтрацией")
    @ApiResponse(responseCode = "200", description = "Список вещей")
    @GetMapping
    public List<ItemResponse> getItems(@ParameterObject ItemFilter filter) {
        return itemService.search(filter).stream()
                .map(ItemMapper::toResponse)
                .toList();
    }

}
