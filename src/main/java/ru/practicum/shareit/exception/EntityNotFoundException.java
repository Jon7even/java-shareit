package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends ApplicationException {
    public EntityNotFoundException(String resource) {
        super(getErrorMessage(resource), HttpStatus.NOT_FOUND);
    }

    private static String getErrorMessage(String resource) {
        return String.format("[%s] not found", resource);
    }
}