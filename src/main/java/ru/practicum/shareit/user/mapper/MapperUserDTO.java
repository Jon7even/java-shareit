package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.dto.UserUpdateInRepositoryDTO;
import ru.practicum.shareit.user.entity.User;

public final class MapperUserDTO {

    private MapperUserDTO() {
    }

    public static User toUserFromUserRequestCreateDTO(UserRequestCreateDTO userRequest) {
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
    }

    public static User toUserFromUserRequestUpdateDTO(UserRequestUpdateDTO userRequest, long userId) {
        return User.builder()
                .id(userId)
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .build();
    }

    public static UserResponseDTO toUserResponseDTOFromUser(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUserFromUserUpdateInRepositoryDTO(UserUpdateInRepositoryDTO userUpdateInRepositoryDTO) {
        return User.builder()
                .id(userUpdateInRepositoryDTO.getId())
                .name(userUpdateInRepositoryDTO.getName())
                .email(userUpdateInRepositoryDTO.getEmail())
                .build();
    }

}
