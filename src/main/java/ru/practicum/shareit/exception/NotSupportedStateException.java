package ru.practicum.shareit.exceptions;

public class NotSupportedStateException extends RuntimeException {
    public NotSupportedStateException(String message) {
        super(message);
    }
}
