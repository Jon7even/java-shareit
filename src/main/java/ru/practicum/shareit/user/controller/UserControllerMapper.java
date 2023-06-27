package ru.practicum.shareit.user.controller;

import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;

public interface UserControllerMapper {
    User toUserFromUserRequestCreateDTO(UserRequestCreateDTO userRequestCreateDTO);

    User toUserFromUserRequestCreateDTO(long userId, UserRequestUpdateDTO userRequestUpdateDTO);

    UserResponseDTO toUserResponseDTOFromUser(User user);
}
