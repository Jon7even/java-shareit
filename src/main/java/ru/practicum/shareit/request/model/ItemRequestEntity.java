package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequestEntity {
    private long id;

    private String description;

    private UserEntity requestor;

    private LocalDateTime created;
}
