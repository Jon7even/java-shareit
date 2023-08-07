package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserEntityTest {
    @Test
    void equalsUser() {
        UserEntity user1 = UserEntity.builder().id(1L).email("2@").name("name").build();
        UserEntity user2 = UserEntity.builder().id(1L).email("2@").name("name").build();

        assertEquals(user1, user2);
    }

}
