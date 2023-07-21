package ru.practicum.shareit.item.projections;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingQueueDTO;

@Getter
@Setter
@AllArgsConstructor
public class ItemIdWithBookingQueue {
    private Long id;

    private BookingQueueDTO lastBooking;

    private BookingQueueDTO nextBooking;
}
