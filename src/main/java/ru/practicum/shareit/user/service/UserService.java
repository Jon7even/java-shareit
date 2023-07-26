package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseTO createUser(UserCreateTO userRequestCreateDTO);

    UserResponseTO findUserById(Optional<Long> idUser);

    UserResponseTO updateUser(UserUpdateTO userRequestUpdateDTO, Optional<Long> idUser);

    void deleteUserById(Optional<Long> idUser);

    List<UserResponseTO> getAllUsers();
}
