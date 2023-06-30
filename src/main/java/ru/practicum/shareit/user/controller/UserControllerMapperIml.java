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

    @Override
    public User toUserFromUserRequestCreateDTO(UserRequestCreateDTO userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
    }

    @Override
    public User toUserFromUserRequestUpdateDTO(UserRequestUpdateDTO userRequest, long userId) {
        return User.builder()
                .id(userId)
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
    }

    @Override
    public UserResponseDTO toUserResponseDTOFromUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

}