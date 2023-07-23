package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingQueueDTO;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseBookingAndCommentDTO {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingQueueDTO lastBooking;

    private BookingQueueDTO nextBooking;

    private List<CommentResponseDTO> comments;
}