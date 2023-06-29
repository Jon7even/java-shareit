package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateInServiceDTO {
    private String name;

    private String description;

    private boolean available;

    @NotNull
    private Long owner;
}