package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Boolean available;

    @NotNull
    private Long owner;
}