package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingState;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestListTO {
    @NotNull
    @Positive
    private Long idUser;
    @NotNull
    private BookingState state;
    private Optional<Integer> from;
    private Optional<Integer> size;
}
