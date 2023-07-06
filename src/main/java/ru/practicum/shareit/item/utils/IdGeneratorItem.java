package ru.practicum.shareit.item.utils;

import org.springframework.stereotype.Component;

@Component
public class IdGeneratorItem {
    private long id;

    public IdGeneratorItem() {
        this.id = 0;
    }

    public long generateId() {
        return ++id;
    }
}
