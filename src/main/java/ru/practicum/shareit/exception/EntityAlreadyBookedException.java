package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;

public class EntityAlreadyBookedException extends ApplicationException {
    public EntityAlreadyBookedException(String resource) {
        super(getErrorMessage(resource), HttpStatus.BAD_REQUEST);
    }

    private static String getErrorMessage(String resource) {
        return String.format("[%s] already booked on this time", resource);
    }
}