package ru.dmsmirnov.rent.domain.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CategoryService.class)
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Test
    void createAndFindCategory() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Стройка");
        category.setDescription("Инструменты");

        CategoryEntity saved = categoryService.create(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(categoryService.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(CategoryEntity::getName)
                .isEqualTo("Стройка");
        assertThat(categoryService.findAll()).hasSize(1);
    }

    @Test
    void updateAndDeleteCategory() {
        CategoryEntity category = categoryService.create(newCategory("Съёмка", "Камеры"));

        category.setDescription("Обновлённое описание");
        CategoryEntity updated = categoryService.update(category);

        assertThat(updated.getDescription()).isEqualTo("Обновлённое описание");

        categoryService.deleteById(updated.getId());
        assertThat(categoryService.findById(updated.getId())).isEmpty();
    }

    private static CategoryEntity newCategory(String name, String description) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setDescription(description);
        return category;
    }

}
