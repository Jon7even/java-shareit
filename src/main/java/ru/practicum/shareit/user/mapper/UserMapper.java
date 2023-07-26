package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserCreateTO;
import ru.practicum.shareit.user.dto.UserResponseTO;
import ru.practicum.shareit.user.dto.UserUpdateTO;
import ru.practicum.shareit.user.model.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "userRequestCreateDTO.name", target = "name")
    @Mapping(source = "userRequestCreateDTO.email", target = "email")
    UserEntity toEntityFromDTOCreate(UserCreateTO userRequestCreateDTO);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "userRequestUpdateDTO.name", target = "name")
    @Mapping(source = "userRequestUpdateDTO.email", target = "email")
    UserEntity toEntityFromDTOUpdate(UserUpdateTO userRequestUpdateDTO, Long userId);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    UserResponseTO toDTOResponseFromEntity(UserEntity user);
}
