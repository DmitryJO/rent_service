package ru.dmsmirnov.rent.domain.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super("Category %d not found".formatted(categoryId));
    }

}
