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
    private long id;

    private String name;

    private String description;

    private boolean available;

    private User owner;
}
