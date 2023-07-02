package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserResponseDTO createUser(UserRequestCreateDTO userRequestCreateDTO);

    UserResponseDTO findUserById(Optional<Long> idUser);

    UserResponseDTO updateUser(UserRequestUpdateDTO userRequestUpdateDTO, Optional<Long> idUser);

    void deleteUserById(Optional<Long> idUser);

    List<UserResponseDTO> getAllUsers();
}
