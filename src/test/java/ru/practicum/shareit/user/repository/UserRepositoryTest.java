package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.setup.GenericRepositoryTest;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryTest extends GenericRepositoryTest {
    @Test
    void saveUser() {
        assertNotNull(userInDB);
        assertEquals(id, userInDB.getId());
        assertEquals(userEntity.getName(), userInDB.getName());
        assertEquals(userEntity.getEmail(), userInDB.getEmail());
    }

    @Test
    void findUser() {
        Optional<UserEntity> result = userRepository.findById(userInDB.getId());

        assertNotNull(result);
        assertEquals(result.get().getId(), userInDB.getId());
        assertEquals(result.get().getName(), userInDB.getName());
        assertEquals(result.get().getEmail(), userInDB.getEmail());
    }

    @Test
    void findUserEntityByEmailContainingIgnoreCase() {
        Optional<UserEntity> result = userRepository.findUserEntityByEmailContainingIgnoreCase(userInDB.getEmail());

        assertNotNull(result);
        assertEquals(result.get().getId(), userInDB.getId());
        assertEquals(result.get().getName(), userInDB.getName());
        assertEquals(result.get().getEmail(), userInDB.getEmail());
    }

}
