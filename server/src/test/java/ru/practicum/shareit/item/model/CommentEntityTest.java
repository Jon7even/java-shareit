package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentEntityTest {
    @Test
    void equalsItemRequestEntity() {
        LocalDateTime currentTime = LocalDateTime.now().withNano(0);
        CommentEntity commentEntity1 = CommentEntity.builder().id(1L).text("Text")
                .item(ItemEntity.builder().id(1L).name("Name").description("desc")
                        .available(true)
                        .user(UserEntity.builder().id(1L).email("2@").name("name").build())
                        .request(ItemRequestEntity.builder().id(1L).description("desc")
                                .created(currentTime)
                                .requestor(UserEntity.builder().id(1L).email("2@").name("name").build())
                                .build())
                        .build())
                .user(UserEntity.builder().id(1L).email("2@").name("name").build())
                .created(currentTime)
                .build();
        CommentEntity commentEntity2 = CommentEntity.builder().id(1L).text("Text")
                .item(ItemEntity.builder().id(1L).name("Name").description("desc")
                        .available(true)
                        .user(UserEntity.builder().id(1L).email("2@").name("name").build())
                        .request(ItemRequestEntity.builder().id(1L).description("desc")
                                .created(currentTime)
                                .requestor(UserEntity.builder().id(1L).email("2@").name("name").build())
                                .build())
                        .build())
                .user(UserEntity.builder().id(1L).email("2@").name("name").build())
                .created(currentTime)
                .build();

        assertEquals(commentEntity1, commentEntity2);
    }

}
