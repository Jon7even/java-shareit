package ru.practicum.shareit.user.controller;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;

@Component
public class UserControllerMapperIml implements UserControllerMapper {
    private UserControllerMapperIml() {
    }

    public User toUserFromUserRequestCreateDTO(UserRequestCreateDTO userRequestCreateDTO) {
        return User.builder()
                .name(userRequestCreateDTO.getName())
                .email(userRequestCreateDTO.getEmail())
                .build();
    }

    public User toUserFromUserRequestCreateDTO(long userId, UserRequestUpdateDTO userRequestUpdateDTO) {
        return User.builder()
                .id(userId)
                .name(userRequestUpdateDTO.getName())
                .email(userRequestUpdateDTO.getEmail())
                .build();
    }

    public UserResponseDTO toUserResponseDTOFromUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}
