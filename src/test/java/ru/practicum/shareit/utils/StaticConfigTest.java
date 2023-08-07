package ru.practicum.shareit.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.config.StaticConfig.DEFAULT_COUNT_SIZE;

public class StaticConfigTest {
    @Test
    void defaultCountSizePages_shouldBe50() {
        assertNotNull(DEFAULT_COUNT_SIZE);
        assertEquals(DEFAULT_COUNT_SIZE, 50);
    }
}
