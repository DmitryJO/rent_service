package ru.dmsmirnov.rent.domain.service;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.CreateOrderRequest;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;
import ru.dmsmirnov.rent.domain.exception.InsufficientAvailabilityException;
import ru.dmsmirnov.rent.domain.exception.InvalidOrderPeriodException;
import ru.dmsmirnov.rent.domain.exception.InvalidOrderStatusTransitionException;
import ru.dmsmirnov.rent.domain.exception.ItemNotFoundException;
import ru.dmsmirnov.rent.domain.exception.OrderNotFoundException;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.RentalOrderEntity;
import ru.dmsmirnov.rent.infrastructure.store.repository.ItemRepository;
import ru.dmsmirnov.rent.infrastructure.store.repository.RentalOrderRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalOrderService {

    private static final Set<RentalOrderStatus> ACTIVE_STATUSES =
            EnumSet.of(RentalOrderStatus.PENDING, RentalOrderStatus.CONFIRMED);

    private final RentalOrderRepository rentalOrderRepository;
    private final ItemRepository itemRepository;

    public List<RentalOrderEntity> findAll() {
        return rentalOrderRepository.findAll();
    }

    public Optional<RentalOrderEntity> findById(Long id) {
        return rentalOrderRepository.findById(id);
    }

    public List<RentalOrderEntity> findByUserId(Long userId) {
        return rentalOrderRepository.findByUserId(userId);
    }

    public List<RentalOrderEntity> findByStatus(RentalOrderStatus status) {
        return rentalOrderRepository.findByStatus(status);
    }

    @Transactional
    public RentalOrderEntity createOrder(CreateOrderRequest request) {
        validatePeriod(request);

        ItemEntity item = itemRepository.findByIdForUpdate(request.itemId())
                .orElseThrow(() -> new ItemNotFoundException(request.itemId()));

        if (item.getQuantity() <= 0) {
            throw new InsufficientAvailabilityException(request.itemId());
        }

        long overlappingOrders = rentalOrderRepository.countOverlappingOrders(
                request.itemId(),
                request.startDate(),
                request.endDate(),
                ACTIVE_STATUSES);

        if (overlappingOrders >= item.getQuantity()) {
            throw new InsufficientAvailabilityException(request.itemId());
        }

        RentalOrderEntity order = new RentalOrderEntity();
        order.setItem(item);
        order.setCustomerFullName(request.customerFullName());
        order.setCustomerPhone(request.customerPhone());
        order.setStartDate(request.startDate());
        order.setEndDate(request.endDate());
        order.setStatus(RentalOrderStatus.PENDING);

        return rentalOrderRepository.save(order);
    }

    @Transactional
    public RentalOrderEntity updateStatus(Long orderId, RentalOrderStatus newStatus) {
        RentalOrderEntity order = rentalOrderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (!isTransitionAllowed(order.getStatus(), newStatus)) {
            throw new InvalidOrderStatusTransitionException(order.getStatus(), newStatus);
        }

        order.setStatus(newStatus);
        return rentalOrderRepository.save(order);
    }

    @Transactional
    public RentalOrderEntity cancelOrder(Long orderId) {
        return updateStatus(orderId, RentalOrderStatus.CANCELLED);
    }

    @Transactional
    public RentalOrderEntity create(RentalOrderEntity order) {
        return rentalOrderRepository.save(order);
    }

    @Transactional
    public RentalOrderEntity update(RentalOrderEntity order) {
        return rentalOrderRepository.save(order);
    }

    @Transactional
    public void deleteById(Long id) {
        rentalOrderRepository.deleteById(id);
    }

    private void validatePeriod(CreateOrderRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new InvalidOrderPeriodException();
        }
    }

    private boolean isTransitionAllowed(RentalOrderStatus from, RentalOrderStatus to) {
        if (from == to) {
            return true;
        }
        return switch (from) {
            case PENDING -> to == RentalOrderStatus.CONFIRMED || to == RentalOrderStatus.CANCELLED;
            case CONFIRMED -> to == RentalOrderStatus.COMPLETED || to == RentalOrderStatus.CANCELLED;
            case CANCELLED, COMPLETED -> false;
        };
    }

}
