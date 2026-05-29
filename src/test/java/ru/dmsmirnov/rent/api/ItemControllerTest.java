package ru.dmsmirnov.rent.api;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.domain.service.CategoryService;
import ru.dmsmirnov.rent.domain.service.ItemService;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

        itemService.create(newItem("Дрель", new BigDecimal("500.00"), 5));
        itemService.create(newItem("Перфоратор", new BigDecimal("1500.00"), 0));
    }

    @Test
    void getItems_returnsAllItems() throws Exception {
        mockMvc.perform(get("/api/v1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryName").value("Стройка"));
    }

    @Test
    void getItems_filtersByCategory() throws Exception {
        mockMvc.perform(get("/api/v1/items").param("categoryId", category.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getItems_filtersByPriceAndAvailability() throws Exception {
        mockMvc.perform(get("/api/v1/items")
                        .param("minPrice", "1000")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/v1/items")
                        .param("maxPrice", "600")
                        .param("availableOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Дрель"));
    }

    private ItemEntity newItem(String name, BigDecimal price, int quantity) {
        ItemEntity item = new ItemEntity();
        item.setCategory(category);
        item.setName(name);
        item.setDescription("Описание");
        item.setCondition(ItemCondition.GOOD);
        item.setPricePerDay(price);
        item.setQuantity(quantity);
        item.setImageUrls(List.of("https://example.com/image.jpg"));
        return item;
    }

}
