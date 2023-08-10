package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.projections.ItemBooking;
import ru.practicum.shareit.user.projections.UserBooker;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseTO {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemBooking item;
    private UserBooker booker;
    private BookingStatus status;
}
