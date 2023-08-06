package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.practicum.shareit.exception.EntityAlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotDeletedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.setup.GenericServiceTest;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        assertThat(result, notNullValue());
        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo(originalDto.getName()));
        assertThat(result.getEmail(), equalTo(originalDto.getEmail()));
        verify(userRepository, times(1)).save(Mockito.any(UserEntity.class));
    }

    @Test
    void findUserById_whenUserExist() {
        initTestVariable(false, false, false);
        when(userRepository.findById(any()))
                .thenReturn(Optional.of(userEntity));

        initOptionalVariable();
        UserResponseTO result = userService.findUserById(idUserOptional);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userEntity.getId()));
        assertThat(result.getName(), equalTo(userEntity.getName()));
        assertThat(result.getEmail(), equalTo(userEntity.getEmail()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void findUserById_whenUserNotExist() {
        when(userRepository.findById(any()))
                .thenReturn(Optional.empty());
        initOptionalVariable();

        assertThrows(EntityNotFoundException.class, () -> userService.findUserById(idUserOptional));
    }

    @Test
    void updateUser_whenThereIsNewDate() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.findUserEntityByEmailContainingIgnoreCase(userEntity.getEmail()))
                .thenReturn(Optional.of(userEntity));

        initTestVariable(false, false, false);
        UserUpdateTO originalDto = UserUpdateTO.builder()
                .name("NameForUpdate")
                .email(userEntity.getEmail())
                .build();

        userEntity.setName(originalDto.getName());
        when(userRepository.save(any()))
                .thenReturn(userEntity);

        initOptionalVariable();
        UserResponseTO result = userService.updateUser(originalDto, idUserOptional);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userEntity.getId()));
        assertThat(result.getName(), equalTo(originalDto.getName()));
        assertThat(result.getEmail(), equalTo(originalDto.getEmail()));
        verify(userRepository, times(1)).save(Mockito.any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findUserEntityByEmailContainingIgnoreCase(anyString());
    }

    @Test
    void updateUser_whenThereIsNotNewDate() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));

        UserUpdateTO originalDto = UserUpdateTO.builder()
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .build();
        initOptionalVariable();
        UserResponseTO resultNotQueryInBd = userService.updateUser(originalDto, idUserOptional);

        assertThat(resultNotQueryInBd, notNullValue());
        assertThat(resultNotQueryInBd.getId(), equalTo(userEntity.getId()));
        assertThat(resultNotQueryInBd.getName(), equalTo(originalDto.getName()));
        assertThat(resultNotQueryInBd.getEmail(), equalTo(originalDto.getEmail()));
        verify(userRepository, never()).save(Mockito.any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateUser_whenThisEmailExistOtherUser() {
        initTestVariable(false, false, false);
        UserEntity otherUser = UserEntity.builder().id(2L).name("otherUser").email(userEntity.getEmail()).build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.findUserEntityByEmailContainingIgnoreCase(userEntity.getEmail()))
                .thenReturn(Optional.of(otherUser));

        initTestVariable(false, false, false);
        UserUpdateTO originalDto = UserUpdateTO.builder()
                .name("NameForUpdate")
                .email(userEntity.getEmail())
                .build();

        initOptionalVariable();
        assertThrows(EntityAlreadyExistsException.class, () -> userService.updateUser(originalDto, idUserOptional));

        verify(userRepository, never()).save(Mockito.any(UserEntity.class));
        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).findUserEntityByEmailContainingIgnoreCase(anyString());
    }

    @Test
    void deleteUserById_whenUserExist() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        initOptionalVariable();
        userService.deleteUserById(idUserOptional);

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void deleteUserById_whenUserExistAndErrorDelete() {
        initTestVariable(false, false, false);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userEntity));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        initOptionalVariable();
        assertThrows(EntityNotDeletedException.class, () -> userService.deleteUserById(idUserOptional));

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).existsById(anyLong());
    }

    @Test
    void getAllUsers_whenListOfOne() {
        initTestVariable(false, false, false);
        List<UserEntity> listFromRepository = List.of(userEntity);
        when(userRepository.findAll())
                .thenReturn(listFromRepository);

        List<UserResponseTO> result = userService.getAllUsers();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.get(0).getId(), equalTo(userEntity.getId()));
        assertThat(result.get(0).getName(), equalTo(userEntity.getName()));
        assertThat(result.get(0).getEmail(), equalTo(userEntity.getEmail()));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_whenListEmpty() {
        initTestVariable(false, false, false);
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<UserResponseTO> result = userService.getAllUsers();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
        verify(userRepository, times(1)).findAll();
    }

}
