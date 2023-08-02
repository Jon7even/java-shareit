package ru.practicum.shareit.item.projections;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class ItemShort {
    private Long id;
    private String name;
    private String description;
    private boolean available;
}
