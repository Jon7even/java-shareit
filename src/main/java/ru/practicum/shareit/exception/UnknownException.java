package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class UnknownException extends ApplicationException {
    public UnknownException(String resource) {
        super(getErrorMessage(resource), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String getErrorMessage(String resource) {
        return String.format("[%s] unknown error", resource);
    }
}
