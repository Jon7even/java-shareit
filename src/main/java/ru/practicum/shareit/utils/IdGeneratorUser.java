package ru.practicum.shareit.utils;

public final class IdGeneratorUser {
    private static int id;

    private IdGeneratorUser() {
        id = 0;
    }

    public static int generateId() {
        id++;
        return id;
    }
}
