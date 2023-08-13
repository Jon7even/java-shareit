package ru.practicum.shareit.item.dto;

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
public class ItemRequestListTO {
    @NotNull
    @Positive
    private Long idUser;
    private Optional<Integer> from;
    private Optional<Integer> size;
    private Optional<String> text;
}
