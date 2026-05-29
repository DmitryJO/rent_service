package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.CategoryResponse;
import ru.dmsmirnov.rent.api.mapper.CategoryMapper;
import ru.dmsmirnov.rent.domain.service.CategoryService;

@Tag(name = "Categories", description = "Категории вещей")
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Список категорий")
    @ApiResponse(responseCode = "200", description = "Список категорий")
    @GetMapping
    public List<CategoryResponse> getCategories() {
        return categoryService.findAll().stream()
                .map(CategoryMapper::toResponse)
                .toList();
    }

}