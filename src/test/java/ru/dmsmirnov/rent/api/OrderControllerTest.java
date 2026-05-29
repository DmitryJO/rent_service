package ru.dmsmirnov.rent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.CreateOrderRequest;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.domain.service.CategoryService;
import ru.dmsmirnov.rent.domain.service.ItemService;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    private ItemEntity item;

    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Съёмка");
        category.setDescription("Камеры");
        category = categoryService.create(category);

        ItemEntity newItem = new ItemEntity();
        newItem.setCategory(category);
        newItem.setName("Камера");
        newItem.setDescription("Зеркальная");
        newItem.setCondition(ItemCondition.NEW);
        newItem.setPricePerDay(new BigDecimal("1200.00"));
        newItem.setQuantity(1);
        newItem.setImageUrls(List.of());
        item = itemService.create(newItem);
    }

    @Test
    void createOrder_returnsCreatedOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                item.getId(),
                "Иван Иванов",
                "+79990001122",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-05"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.itemId").value(item.getId()))
                .andExpect(jsonPath("$.customerFullName").value("Иван Иванов"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createOrder_returnsConflictWhenUnavailable() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(
                item.getId(),
                "Иван Иванов",
                "+79990001122",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-05"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        CreateOrderRequest duplicate = new CreateOrderRequest(
                item.getId(),
                "Пётр Петров",
                "+79990003344",
                LocalDate.parse("2026-06-02"),
                LocalDate.parse("2026-06-04"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

}
