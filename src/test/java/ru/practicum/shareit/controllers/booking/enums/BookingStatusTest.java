package ru.practicum.shareit.controllers.booking.enums;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingStatusTest {
    @Test
    void valueOf() {
        assertEquals(BookingStatus.WAITING, BookingStatus.valueOf("WAITING"));
        assertEquals(BookingStatus.APPROVED, BookingStatus.valueOf("APPROVED"));
        assertEquals(BookingStatus.REJECTED, BookingStatus.valueOf("REJECTED"));
        assertEquals(BookingStatus.CANCELED, BookingStatus.valueOf("CANCELED"));
    }
}
