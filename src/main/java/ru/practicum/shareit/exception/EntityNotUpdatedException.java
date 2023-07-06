package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EntityNotUpdatedException extends ApplicationException {
    public EntityNotUpdatedException(String resource) {
        super(getErrorMessage(resource), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static String getErrorMessage(String resource) {
        return String.format("[%s] not updated", resource);
    }
}
