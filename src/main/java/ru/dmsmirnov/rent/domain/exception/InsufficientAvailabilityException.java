package ru.dmsmirnov.rent.domain.exception;

public class InsufficientAvailabilityException extends RuntimeException {

    public InsufficientAvailabilityException(Long itemId) {
        super("Item %d is not available for the selected period".formatted(itemId));
    }

}
