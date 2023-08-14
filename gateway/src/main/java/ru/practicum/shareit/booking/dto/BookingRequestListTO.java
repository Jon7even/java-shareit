package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestListTO {
    @NotNull
    @Positive
    private long idUser;

    @NotNull
    private BookingState state;

    private int from;

    private int size;
}
