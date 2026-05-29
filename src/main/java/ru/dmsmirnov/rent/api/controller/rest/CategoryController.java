package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories", description = "Категории вещей")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Operation(summary = "Список категорий")
    @ApiResponse(responseCode = "200", description = "Список категорий (может быть пустым)")
    @GetMapping
    public List<Object> getCategories() {
        return Collections.emptyList();
    }

}
