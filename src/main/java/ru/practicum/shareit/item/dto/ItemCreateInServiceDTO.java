package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemCreateInServiceDTO {
    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private boolean available;

    @NotNull
    private Long owner;
}