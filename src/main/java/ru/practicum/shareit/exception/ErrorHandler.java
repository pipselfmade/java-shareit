package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ru.practicum.shareit.exceptions.ErrorResponse handleNotFoundException(final ru.practicum.shareit.exceptions.NotFoundException e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse("Not found error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.practicum.shareit.exceptions.ErrorResponse validationException(final MethodArgumentNotValidException e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.practicum.shareit.exceptions.ErrorResponse notAvailableException(final ru.practicum.shareit.exceptions.NotAvailableException e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse("Available error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ru.practicum.shareit.exceptions.ErrorResponse duplicateException(final CloneNotSupportedException e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse("Duplicate error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ru.practicum.shareit.exceptions.ErrorResponse handleThrowable(final Throwable e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse("Internal Server error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ru.practicum.shareit.exceptions.ErrorResponse handleWrongState(final ru.practicum.shareit.exceptions.NotSupportedStateException e) {
        return new ru.practicum.shareit.exceptions.ErrorResponse(e.getMessage(), e.getMessage());
    }
}
