package ru.dmsmirnov.rent.domain.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(Long itemId) {
        super("Item %d not found".formatted(itemId));
    }

}
