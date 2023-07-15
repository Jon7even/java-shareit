package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserRequestCreateDTO;
import ru.practicum.shareit.user.dto.UserRequestUpdateDTO;
import ru.practicum.shareit.user.dto.UserResponseDTO;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "userRequestCreateDTO.name", target = "name")
    @Mapping(source = "userRequestCreateDTO.email", target = "email")
    User toEntityFromDTOCreate(UserRequestCreateDTO userRequestCreateDTO);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "userRequestUpdateDTO.name", target = "name")
    @Mapping(source = "userRequestUpdateDTO.email", target = "email")
    User toEntityFromDTOUpdate(UserRequestUpdateDTO userRequestUpdateDTO, Long userId);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    UserResponseDTO toDTOResponseFromEntity(User user);
}
