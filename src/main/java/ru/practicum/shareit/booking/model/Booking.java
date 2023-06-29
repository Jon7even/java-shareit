package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.entity.Item;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@Builder
public class Booking {
    private long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Item item;

    private BookingStatus status;
}
