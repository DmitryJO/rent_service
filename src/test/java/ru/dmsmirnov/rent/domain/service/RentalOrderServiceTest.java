package ru.dmsmirnov.rent.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.dmsmirnov.rent.api.dto.CreateOrderRequest;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.RentalOrderEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({CategoryService.class, ItemService.class, RentalOrderService.class})
class RentalOrderServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private RentalOrderService rentalOrderService;

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
        newItem.setQuantity(3);
        newItem.setImageUrls(List.of());
        item = itemService.create(newItem);
    }

    @Test
    void createAndFindOrder() {
        RentalOrderEntity order = newOrder("Иван Иванов", "+79990001122");

        RentalOrderEntity saved = rentalOrderService.create(order);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
        assertThat(rentalOrderService.findById(saved.getId())).isPresent();
        assertThat(rentalOrderService.findByStatus(RentalOrderStatus.PENDING)).hasSize(1);
        assertThat(rentalOrderService.findAll()).hasSize(1);
    }

    @Test
    void updateAndDeleteOrder() {
        RentalOrderEntity saved = rentalOrderService.create(newOrder("Пётр Петров", "+79990003344"));

        saved.setStatus(RentalOrderStatus.CONFIRMED);
        RentalOrderEntity updated = rentalOrderService.update(saved);

        assertThat(updated.getStatus()).isEqualTo(RentalOrderStatus.CONFIRMED);
        assertThat(updated.getVersion()).isGreaterThanOrEqualTo(0L);

        rentalOrderService.deleteById(updated.getId());
        assertThat(rentalOrderService.findById(updated.getId())).isEmpty();
    }

    @Test
    void updateStatus_confirmsPendingOrder() {
        RentalOrderEntity saved = rentalOrderService.create(newOrder("Иван Иванов", "+79990001122"));

        RentalOrderEntity confirmed = rentalOrderService.updateStatus(saved.getId(), RentalOrderStatus.CONFIRMED);

        assertThat(confirmed.getStatus()).isEqualTo(RentalOrderStatus.CONFIRMED);
    }

    @Test
    void cancelOrder_freesAvailabilityForSamePeriod() {
        RentalOrderEntity saved = rentalOrderService.create(newOrder("Иван Иванов", "+79990001122"));

        rentalOrderService.cancelOrder(saved.getId());

        RentalOrderEntity replacement = rentalOrderService.createOrder(new CreateOrderRequest(
                item.getId(),
                "Пётр Петров",
                "+79990003344",
                LocalDate.parse("2026-06-02"),
                LocalDate.parse("2026-06-04")));

        assertThat(replacement.getId()).isNotNull();
        assertThat(replacement.getStatus()).isEqualTo(RentalOrderStatus.PENDING);
    }

    private RentalOrderEntity newOrder(String fullName, String phone) {
        RentalOrderEntity order = new RentalOrderEntity();
        order.setItem(item);
        order.setCustomerFullName(fullName);
        order.setCustomerPhone(phone);
        order.setStartDate(LocalDate.parse("2026-06-01"));
        order.setEndDate(LocalDate.parse("2026-06-05"));
        order.setStatus(RentalOrderStatus.PENDING);
        return order;
    }

}
