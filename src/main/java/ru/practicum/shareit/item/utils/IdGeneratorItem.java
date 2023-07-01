package ru.practicum.shareit.item.utils;

public final class IdGeneratorItem {
    private static int id;

    private IdGeneratorItem() {
        id = 0;
    }

    public static int generateId() {
        id++;
        return id;
    }
}
