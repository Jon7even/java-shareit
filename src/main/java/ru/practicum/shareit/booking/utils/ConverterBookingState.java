package ru.practicum.shareit.booking.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.entity.BookingState;
import ru.practicum.shareit.exception.UnknownException;

@Component
@Slf4j
public class ConverterBookingState implements Converter<String, BookingState> {
    @Override
    public BookingState convert(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (RuntimeException e) {
            log.error("An attempt was made to insert an incorrect Booking State [{}]", value);
            throw new UnknownException("UNSUPPORTED_STATUS");
        }
    }
}
