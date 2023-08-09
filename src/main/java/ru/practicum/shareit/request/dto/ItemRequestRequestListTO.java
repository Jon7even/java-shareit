package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestRequestListTO {
    @NotNull
    @Positive
    private Long idUser;
    private Optional<Integer> from;
    private Optional<Integer> size;
}
