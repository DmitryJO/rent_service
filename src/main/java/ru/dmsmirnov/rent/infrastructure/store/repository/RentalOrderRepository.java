package ru.dmsmirnov.rent.infrastructure.store.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;
import ru.dmsmirnov.rent.infrastructure.store.entity.RentalOrderEntity;

public interface RentalOrderRepository extends JpaRepository<RentalOrderEntity, Long> {

    List<RentalOrderEntity> findByUserId(Long userId);

    List<RentalOrderEntity> findByStatus(RentalOrderStatus status);

    @Query("""
            SELECT COUNT(r) FROM RentalOrderEntity r
            WHERE r.item.id = :itemId
            AND r.status IN :statuses
            AND r.startDate <= :endDate
            AND r.endDate >= :startDate
            """)
    long countOverlappingOrders(
            @Param("itemId") Long itemId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuses") Collection<RentalOrderStatus> statuses);

}
