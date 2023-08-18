package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingQueueTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseBookingAndCommentTO {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingQueueTO lastBooking;

    private BookingQueueTO nextBooking;

    private List<CommentResponseTO> comments;
}