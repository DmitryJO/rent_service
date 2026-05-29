package ru.dmsmirnov.rent.infrastructure.store.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByName(String name);

}
