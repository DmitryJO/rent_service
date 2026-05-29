package ru.dmsmirnov.rent.infrastructure.store.repository;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ru.dmsmirnov.rent.api.dto.ItemFilter;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;

public final class ItemSpecifications {

    private ItemSpecifications() {
    }

    public static Specification<ItemEntity> withFilter(ItemFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if (query != null) {
                root.fetch("category", JoinType.INNER);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filter.categoryId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), filter.categoryId()));
            }
            if (filter.minPrice() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("pricePerDay"), filter.minPrice()));
            }
            if (filter.maxPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("pricePerDay"), filter.maxPrice()));
            }
            if (filter.minQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), filter.minQuantity()));
            }
            if (Boolean.TRUE.equals(filter.availableOnly())) {
                predicates.add(criteriaBuilder.greaterThan(root.get("quantity"), 0));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

}
