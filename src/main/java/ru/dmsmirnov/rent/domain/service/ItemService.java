package ru.dmsmirnov.rent.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.dmsmirnov.rent.api.dto.CreateItemRequest;
import ru.dmsmirnov.rent.api.dto.ItemFilter;
import ru.dmsmirnov.rent.api.dto.UpdateItemRequest;
import ru.dmsmirnov.rent.domain.exception.CategoryNotFoundException;
import ru.dmsmirnov.rent.domain.exception.ItemNotFoundException;
import ru.dmsmirnov.rent.domain.enums.ItemCondition;
import ru.dmsmirnov.rent.infrastructure.store.entity.CategoryEntity;
import ru.dmsmirnov.rent.infrastructure.store.entity.ItemEntity;
import ru.dmsmirnov.rent.infrastructure.store.repository.CategoryRepository;
import ru.dmsmirnov.rent.infrastructure.store.repository.ItemRepository;
import ru.dmsmirnov.rent.infrastructure.store.repository.ItemSpecifications;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;

    @Cacheable("items")
    public List<ItemEntity> findAll() {
        return itemRepository.findAll();
    }

    @Cacheable("items")
    public Optional<ItemEntity> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Optional<ItemEntity> findByIdWithCategory(Long id) {
        return itemRepository.findByIdWithCategory(id);
    }

    public List<ItemEntity> findAllWithCategory() {
        return itemRepository.findAllWithCategory();
    }

    @Cacheable("items")
    public List<ItemEntity> findByCategoryId(Long categoryId) {
        return itemRepository.findByCategoryId(categoryId);
    }

    @Cacheable("items")
    public List<ItemEntity> search(ItemFilter filter) {
        return itemRepository.findAll(ItemSpecifications.withFilter(filter));
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public ItemEntity createItem(CreateItemRequest request) {
        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        ItemEntity item = new ItemEntity();
        item.setCategory(category);
        item.setName(request.name());
        item.setDescription(request.description());
        item.setCondition(request.condition() != null ? request.condition() : ItemCondition.GOOD);
        item.setPricePerDay(request.pricePerDay());
        item.setQuantity(request.quantity());
        item.setImageUrls(request.imageUrls() != null ? request.imageUrls() : new ArrayList<>());

        return itemRepository.save(item);
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public ItemEntity updateItem(Long id, UpdateItemRequest request) {
        ItemEntity item = itemRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        if (request.categoryId() != null) {
            CategoryEntity category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
            item.setCategory(category);
        }
        if (request.name() != null) {
            item.setName(request.name());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.condition() != null) {
            item.setCondition(request.condition());
        }
        if (request.pricePerDay() != null) {
            item.setPricePerDay(request.pricePerDay());
        }
        if (request.quantity() != null) {
            item.setQuantity(request.quantity());
        }
        if (request.imageUrls() != null) {
            item.setImageUrls(request.imageUrls());
        }

        return itemRepository.save(item);
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public void deleteItem(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ItemNotFoundException(id);
        }
        itemRepository.deleteById(id);
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public ItemEntity create(ItemEntity item) {
        return itemRepository.save(item);
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public ItemEntity update(ItemEntity item) {
        return itemRepository.save(item);
    }

    @CacheEvict(value = "items", allEntries = true)
    @Transactional
    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }

}
