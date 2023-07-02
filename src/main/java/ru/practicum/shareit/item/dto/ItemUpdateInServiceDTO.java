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
public class ItemUpdateInServiceDTO {
    @NotNull
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    @NotNull
    private Long owner;
}