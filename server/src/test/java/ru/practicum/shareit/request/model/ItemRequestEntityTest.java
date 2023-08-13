package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestEntityTest {
    @Test
    void equalsItemRequestEntity() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        ItemRequestEntity itemRequestEntity1 = ItemRequestEntity.builder().id(1L).description("desc")
                .created(currentTime)
                .requestor(UserEntity.builder().id(1L).email("2@").name("name").build())
                .build();
        ItemRequestEntity itemRequestEntity2 = ItemRequestEntity.builder().id(1L).description("desc")
                .created(currentTime)
                .requestor(UserEntity.builder().id(1L).email("2@").name("name").build())
                .build();

        assertEquals(itemRequestEntity1, itemRequestEntity2);
    }

}
