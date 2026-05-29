package ru.dmsmirnov.rent.infrastructure.store.repository;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, Long>, JpaSpecificationExecutor<ItemEntity> {

    List<ItemEntity> findByCategoryId(Long categoryId);

    @Query("SELECT i FROM ItemEntity i JOIN FETCH i.category")
    List<ItemEntity> findAllWithCategory();

    @Query("SELECT i FROM ItemEntity i JOIN FETCH i.category WHERE i.id = :id")
    Optional<ItemEntity> findByIdWithCategory(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemEntity i WHERE i.id = :id")
    Optional<ItemEntity> findByIdForUpdate(@Param("id") Long id);

}
