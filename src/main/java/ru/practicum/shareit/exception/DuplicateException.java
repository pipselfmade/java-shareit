package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateException extends CloneNotSupportedException {
    public DuplicateException(String message) {
        super(message);
    }
}
