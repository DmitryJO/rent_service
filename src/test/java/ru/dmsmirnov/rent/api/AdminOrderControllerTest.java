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
import ru.dmsmirnov.rent.api.dto.UpdateOrderStatusRequest;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;
import ru.dmsmirnov.rent.domain.service.CategoryService;
import ru.dmsmirnov.rent.domain.service.ItemService;
import ru.dmsmirnov.rent.domain.service.RentalOrderService;
import ru.dmsmirnov.rent.infrastructure.configuration.AdminRoleStubFilter;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.RentalOrderEntity;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude="
                + "org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,"
                + "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
@AutoConfigureMockMvc
@Transactional
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RentalOrderService rentalOrderService;

    private RentalOrderEntity order;

    @BeforeEach
    void setUp() {
        CategoryEntity category = new CategoryEntity();
        category.setName("Съёмка");
        category.setDescription("Камеры");
        category = categoryService.create(category);

        ItemEntity item = new ItemEntity();
        item.setCategory(category);
        item.setName("Камера");
        item.setDescription("Зеркальная");
        item.setCondition(ItemCondition.NEW);
        item.setPricePerDay(new BigDecimal("1200.00"));
        item.setQuantity(1);
        item.setImageUrls(List.of());
        item = itemService.create(item);

        order = rentalOrderService.createOrder(new CreateOrderRequest(
                item.getId(),
                "Иван Иванов",
                "+79990001122",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-05")));
    }

    @Test
    void getOrders_withoutAdminRole_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    @Test
    void getOrders_withAdminStub_returnsOrders() throws Exception {
        mockMvc.perform(get("/api/v1/admin/orders")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void updateStatus_withAdminStub_confirmsOrder() throws Exception {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(RentalOrderStatus.CONFIRMED);

        mockMvc.perform(patch("/api/v1/admin/orders/{id}/status", order.getId())
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void cancelOrder_withAdminStub_cancelsOrder() throws Exception {
        mockMvc.perform(post("/api/v1/admin/orders/{id}/cancel", order.getId())
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

}
