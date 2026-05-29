package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Items", description = "Каталог арендуемых вещей")
@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Operation(summary = "Список вещей")
    @ApiResponse(responseCode = "200", description = "Список вещей (может быть пустым)")
    @GetMapping
    public List<Object> getItems() {
        return Collections.emptyList();
    }

}
