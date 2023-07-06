package ru.practicum.shareit.request.entity;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@Builder
public class ItemRequest {
    private long id;

    private String description;

    private User requestor;

    private LocalDateTime created;
}
