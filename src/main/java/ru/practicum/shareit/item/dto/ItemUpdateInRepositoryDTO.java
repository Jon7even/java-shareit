package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.entity.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateInRepositoryDTO {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private User owner;
}
