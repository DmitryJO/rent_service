package ru.dmsmirnov.rent.domain.exception;

import ru.dmsmirnov.rent.domain.enums.RentalOrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(RentalOrderStatus from, RentalOrderStatus to) {
        super("Cannot change order status from %s to %s".formatted(from, to));
    }

}
