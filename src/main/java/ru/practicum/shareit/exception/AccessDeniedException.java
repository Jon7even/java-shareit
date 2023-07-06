package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApplicationException {
    public AccessDeniedException(String resource) {
        super(getErrorMessage(resource), HttpStatus.FORBIDDEN);
    }

    private static String getErrorMessage(String resource) {
        return String.format("Access denied for [%s]", resource);
    }
}
