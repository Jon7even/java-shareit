package ru.practicum.shareit.booking.enums;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStateTest {
    @Test
    void valueOf() {
        assertEquals(BookingState.ALL, BookingState.valueOf("ALL"));
        assertEquals(BookingState.CURRENT, BookingState.valueOf("CURRENT"));
        assertEquals(BookingState.PAST, BookingState.valueOf("PAST"));
        assertEquals(BookingState.FUTURE, BookingState.valueOf("FUTURE"));
        assertEquals(BookingState.WAITING, BookingState.valueOf("WAITING"));
        assertEquals(BookingState.REJECTED, BookingState.valueOf("REJECTED"));
    }
}

