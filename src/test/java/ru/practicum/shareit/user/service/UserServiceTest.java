package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.setup.GenericServiceTest;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.model.UserEntity;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest extends GenericServiceTest {
    protected UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceIml(userRepository);
    }

    @Test
    void createUser() {
        initTestVariable(false, false, false);
        when(userRepository.save(any()))
                .thenReturn(userEntity);

        UserCreateTO originalDto = UserCreateTO.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
        UserResponseTO result = userService.createUser(originalDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(originalDto.getName()));
        assertThat(result.getEmail(), equalTo(originalDto.getEmail()));
        verify(userRepository, times(1)).save(Mockito.any(UserEntity.class));
    }

}
