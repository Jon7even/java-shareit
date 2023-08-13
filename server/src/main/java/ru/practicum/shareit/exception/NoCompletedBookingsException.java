package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class NoCompletedBookingsException extends ApplicationException {
    public NoCompletedBookingsException(String resource) {
        super(resource, HttpStatus.BAD_REQUEST);
    }
}
