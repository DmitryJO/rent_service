package ru.dmsmirnov.rent.domain.service;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.dmsmirnov.rent.api.dto.ItemFilter;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CategoryService.class, ItemService.class})
class ItemServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setName("Стройка");
        newCategory.setDescription("Инструменты");
        category = categoryService.create(newCategory);
    }

    @Test
    void createAndFindItem() {
        ItemEntity item = newItem("Дрель", 5);

        ItemEntity saved = itemService.create(item);

        assertThat(saved.getId()).isNotNull();
        assertThat(itemService.findById(saved.getId())).isPresent();
        assertThat(itemService.findByCategoryId(category.getId())).hasSize(1);
        assertThat(itemService.findAll()).hasSize(1);
    }

    @Test
    void updateAndDeleteItem() {
        ItemEntity saved = itemService.create(newItem("Перфоратор", 2));

        saved.setQuantity(1);
        saved.setPricePerDay(new BigDecimal("1500.00"));
        ItemEntity updated = itemService.update(saved);

        assertThat(updated.getQuantity()).isEqualTo(1);
        assertThat(updated.getPricePerDay()).isEqualByComparingTo("1500.00");

        itemService.deleteById(updated.getId());
        assertThat(itemService.findById(updated.getId())).isEmpty();
    }

    @Test
    void searchFiltersByPriceAndAvailability() {
        itemService.create(newItem("Пила", 3));
        ItemEntity expensive = newItem("Кран", 1);
        expensive.setPricePerDay(new BigDecimal("2000.00"));
        itemService.create(expensive);

        ItemEntity unavailable = newItem("Лестница", 0);
        unavailable.setPricePerDay(new BigDecimal("300.00"));
        itemService.create(unavailable);

        assertThat(itemService.search(new ItemFilter(
                null, new BigDecimal("1000.00"), null, null, true)))
                .extracting(ItemEntity::getName)
                .containsExactly("Кран");

        assertThat(itemService.search(new ItemFilter(
                category.getId(), null, new BigDecimal("600.00"), null, true)))
                .extracting(ItemEntity::getName)
                .containsExactly("Пила");
    }

    private ItemEntity newItem(String name, int quantity) {
        ItemEntity item = new ItemEntity();
        item.setCategory(category);
        item.setName(name);
        item.setDescription("Описание");
        item.setCondition(ItemCondition.GOOD);
        item.setPricePerDay(new BigDecimal("500.00"));
        item.setQuantity(quantity);
        item.setImageUrls(List.of("https://example.com/image.jpg"));
        return item;
    }

}
