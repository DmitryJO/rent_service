package ru.dmsmirnov.rent.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.dmsmirnov.rent.api.dto.CreateOrderRequest;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.domain.exception.InsufficientAvailabilityException;
import ru.dmsmirnov.rent.domain.exception.InvalidOrderPeriodException;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class RentalOrderServiceConcurrencyTest {

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
        category.setName("Съёмка-" + System.nanoTime());
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
    void parallelRequestsOnSingleItem_allowOnlyOneBooking() throws InterruptedException {
        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        LocalDate startDate = LocalDate.parse("2026-06-01");
        LocalDate endDate = LocalDate.parse("2026-06-05");

        for (int i = 0; i < threads; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    start.await();
                    rentalOrderService.createOrder(new CreateOrderRequest(
                            item.getId(),
                            "Клиент " + index,
                            "+7999000" + String.format("%04d", index),
                            startDate,
                            endDate));
                    successCount.incrementAndGet();
                } catch (InsufficientAvailabilityException exception) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertThat(done.await(30, TimeUnit.SECONDS)).isTrue();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(threads - 1);
    }

    @Test
    void createOrder_rejectsInvalidPeriod() {
        assertThatThrownBy(() -> rentalOrderService.createOrder(new CreateOrderRequest(
                item.getId(),
                "Иван Иванов",
                "+79990001122",
                LocalDate.parse("2026-06-05"),
                LocalDate.parse("2026-06-01"))))
                .isInstanceOf(InvalidOrderPeriodException.class);
    }

    @Test
    void createOrder_allowsNonOverlappingPeriods() {
        rentalOrderService.createOrder(new CreateOrderRequest(
                item.getId(),
                "Иван Иванов",
                "+79990001122",
                LocalDate.parse("2026-06-01"),
                LocalDate.parse("2026-06-05")));

        var secondOrder = rentalOrderService.createOrder(new CreateOrderRequest(
                item.getId(),
                "Пётр Петров",
                "+79990003344",
                LocalDate.parse("2026-07-01"),
                LocalDate.parse("2026-07-05")));

        assertThat(secondOrder.getId()).isNotNull();
        assertThat(itemService.findById(item.getId())).get()
                .extracting(ItemEntity::getQuantity)
                .isEqualTo(1);
    }

}
