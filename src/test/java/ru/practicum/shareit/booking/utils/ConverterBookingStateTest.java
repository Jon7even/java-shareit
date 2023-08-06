package ru.practicum.shareit.booking.utils;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.UnknownException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ConverterBookingStateTest {

    @Test
    void testConvert_Valid() {
        ConverterBookingState state = new ConverterBookingState();
        BookingState result = state.convert("WAITING");

        assertThat(result, notNullValue());
        assertThat(result, equalTo(BookingState.WAITING));
    }

    @Test
    void testConvert_Exception() {
        ConverterBookingState state = new ConverterBookingState();

        assertThrows(UnknownException.class, () -> state.convert("TEST"));
    }
}
