package ru.dmsmirnov.rent.domain.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super("Order %d not found".formatted(orderId));
    }

}
