package ru.practicum.shareit.controllers.user.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.controllers.setup.GenericMapperTest;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.UserEntity;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest extends GenericMapperTest {

    @Test
    void toEntityFromDTOCreate() {
        UserCreateTO originalDto = UserCreateTO.builder().name("test").email("test@ya.ru").build();
        UserEntity resultEntity = UserMapper.INSTANCE.toEntityFromDTOCreate(originalDto);

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals(originalDto.getName(), resultEntity.getName());
        assertEquals(originalDto.getEmail(), resultEntity.getEmail());
    }

    @Test
    void toEntityFromDTOUpdate() {
        initTestVariable(false, false, false);
        UserUpdateTO originalDto = UserUpdateTO.builder().name("test").email("test@ya.ru").build();
        UserEntity resultEntity = UserMapper.INSTANCE.toEntityFromDTOUpdate(originalDto, id);

        assertNotNull(resultEntity);
        assertEquals(id, resultEntity.getId());
        assertEquals(originalDto.getName(), resultEntity.getName());
        assertEquals(originalDto.getEmail(), resultEntity.getEmail());
    }

    @Test
    void toDTOResponseFromEntity() {
        initTestVariable(false, false, false);
        UserResponseTO resultDto = UserMapper.INSTANCE.toDTOResponseFromEntity(userEntity);

        assertNotNull(resultDto);
        assertEquals(resultDto.getId(), userEntity.getId());
        assertEquals(resultDto.getName(), userEntity.getName());
        assertEquals(resultDto.getEmail(), userEntity.getEmail());
    }

}
