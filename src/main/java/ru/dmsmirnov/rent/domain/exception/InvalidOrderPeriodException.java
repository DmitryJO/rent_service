package ru.dmsmirnov.rent.domain.exception;

public class InvalidOrderPeriodException extends RuntimeException {

    public InvalidOrderPeriodException() {
        super("Order end date must be on or after start date");
    }

}
