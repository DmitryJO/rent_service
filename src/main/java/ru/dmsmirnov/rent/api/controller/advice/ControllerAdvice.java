package ru.dmsmirnov.rent.api.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.dmsmirnov.rent.api.dto.ErrorResponse;
import ru.dmsmirnov.rent.domain.exception.CategoryNotFoundException;
import ru.dmsmirnov.rent.domain.exception.InsufficientAvailabilityException;
import ru.dmsmirnov.rent.domain.exception.InvalidOrderPeriodException;
import ru.dmsmirnov.rent.domain.exception.InvalidOrderStatusTransitionException;
import ru.dmsmirnov.rent.domain.exception.ItemNotFoundException;
import ru.dmsmirnov.rent.domain.exception.OrderNotFoundException;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFound(ItemNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleCategoryNotFound(CategoryNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InsufficientAvailabilityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleInsufficientAvailability(InsufficientAvailabilityException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InvalidOrderPeriodException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidOrderPeriod(InvalidOrderPeriodException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFound(OrderNotFoundException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(InvalidOrderStatusTransitionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidOrderStatusTransition(InvalidOrderStatusTransitionException exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException exception) {
        return new ErrorResponse("Access denied");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Validation failed");
        return new ErrorResponse(message);
    }

}
