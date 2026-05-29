package ru.dmsmirnov.rent.domain.service;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.ItemFilter;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceCacheTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CacheManager cacheManager;

    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        CategoryEntity newCategory = new CategoryEntity();
        newCategory.setName("Стройка");
        newCategory.setDescription("Инструменты");
        category = categoryService.create(newCategory);
    }

    @Test
    void updateEvictsItemsCache() {
        ItemEntity item = itemService.create(newItem("Дрель", 5));
        ItemFilter filter = new ItemFilter(null, null, null, null, null);

        itemService.search(filter);

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();
        assertThat(cache.get(filter)).isNotNull();

        item.setQuantity(1);
        itemService.update(item);

        assertThat(cache.get(filter)).isNull();
    }

    @Test
    void deleteEvictsItemsCache() {
        ItemEntity item = itemService.create(newItem("Дрель", 5));
        ItemFilter filter = new ItemFilter(null, null, null, null, null);

        itemService.search(filter);

        Cache cache = cacheManager.getCache("items");
        assertThat(cache).isNotNull();
        assertThat(cache.get(filter)).isNotNull();

        itemService.deleteById(item.getId());

        assertThat(cache.get(filter)).isNull();
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
