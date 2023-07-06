package ru.practicum.shareit.user.utils;

import org.springframework.stereotype.Component;

@Component
public class IdGeneratorUser {
    private long id;

    public IdGeneratorUser() {
        this.id = 0;
    }

    public long generateId() {
        return ++id;
    }
}
