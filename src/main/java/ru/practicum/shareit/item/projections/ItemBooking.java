package ru.practicum.shareit.item.projections;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ItemBooking {
    private Long id;
    private String name;
}
