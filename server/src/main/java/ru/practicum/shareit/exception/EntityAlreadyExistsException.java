package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends ApplicationException {
    public EntityAlreadyExistsException(String resource) {
        super(getErrorMessage(resource), HttpStatus.CONFLICT);
    }

    private static String getErrorMessage(String resource) {
        return String.format("[%s] already exists", resource);
    }
}
