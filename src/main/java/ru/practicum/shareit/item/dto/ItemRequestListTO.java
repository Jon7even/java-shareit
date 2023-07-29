package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestListTO {
    private Optional<Long> idUser;
    private Optional<Integer> from;
    private Optional<Integer> size;
    private Optional<String> text;
}
