package ru.dmsmirnov.rent.api.controller.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.dmsmirnov.rent.api.dto.CategoryResponse;
import ru.dmsmirnov.rent.api.dto.CreateCategoryRequest;
import ru.dmsmirnov.rent.api.mapper.CategoryMapper;
import ru.dmsmirnov.rent.domain.service.CategoryService;

@Tag(name = "Admin Categories", description = "Администрирование категорий")
@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Создать категорию")
    @ApiResponse(responseCode = "201", description = "Категория создана")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        return CategoryMapper.toResponse(categoryService.createCategory(request));
    }

}
