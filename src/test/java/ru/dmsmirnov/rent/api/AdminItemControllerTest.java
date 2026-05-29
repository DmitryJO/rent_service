package ru.dmsmirnov.rent.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.CreateCategoryRequest;
import ru.dmsmirnov.rent.api.dto.CreateItemRequest;
import ru.dmsmirnov.rent.api.dto.UpdateItemRequest;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.infrastructure.configuration.AdminRoleStubFilter;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class AdminItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem_withoutAdminRole_returnsForbidden() throws Exception {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Стройка", "Инструменты");
        String categoryJson = mockMvc.perform(post("/api/v1/admin/categories")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(categoryJson).get("id").asLong();

        CreateItemRequest itemRequest = new CreateItemRequest(
                categoryId,
                "Дрель",
                "Мощная дрель",
                ItemCondition.GOOD,
                new BigDecimal("500.00"),
                3,
                List.of("https://example.com/drill.jpg"));

        mockMvc.perform(post("/api/v1/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAndManageItem_withAdminRole() throws Exception {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Съёмка", "Камеры");
        String categoryJson = mockMvc.perform(post("/api/v1/admin/categories")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(categoryJson).get("id").asLong();

        CreateItemRequest itemRequest = new CreateItemRequest(
                categoryId,
                "Камера",
                "Зеркальная",
                ItemCondition.NEW,
                new BigDecimal("1200.00"),
                2,
                List.of());

        String itemJson = mockMvc.perform(post("/api/v1/admin/items")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Камера"))
                .andExpect(jsonPath("$.categoryId").value(categoryId.intValue()))
                .andReturn().getResponse().getContentAsString();

        Long itemId = objectMapper.readTree(itemJson).get("id").asLong();

        mockMvc.perform(get("/api/v1/admin/items/{id}", itemId)
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));

        UpdateItemRequest updateRequest = new UpdateItemRequest(
                null, null, null, null, null, 5, null);

        mockMvc.perform(patch("/api/v1/admin/items/{id}", itemId)
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5));

        mockMvc.perform(delete("/api/v1/admin/items/{id}", itemId)
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/admin/items/{id}", itemId)
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createItem_withoutCondition_usesGoodByDefault() throws Exception {
        CreateCategoryRequest categoryRequest = new CreateCategoryRequest("Спорт", null);
        String categoryJson = mockMvc.perform(post("/api/v1/admin/categories")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long categoryId = objectMapper.readTree(categoryJson).get("id").asLong();

        String itemBody = """
                {
                  "categoryId": %d,
                  "name": "Велосипед",
                  "pricePerDay": 300.00,
                  "quantity": 1
                }
                """.formatted(categoryId);

        mockMvc.perform(post("/api/v1/admin/items")
                        .header(AdminRoleStubFilter.ADMIN_ROLE_HEADER, "ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(itemBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.condition").value("GOOD"));
    }

}
