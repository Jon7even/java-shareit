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
    @Mapping(source = "userCreateTO.name", target = "name")
    @Mapping(source = "userCreateTO.email", target = "email")
    UserEntity toEntityFromDTOCreate(UserCreateTO userCreateTO);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "userUpdateTO.name", target = "name")
    @Mapping(source = "userUpdateTO.email", target = "email")
    UserEntity toEntityFromDTOUpdate(UserUpdateTO userUpdateTO, Long userId);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.email", target = "email")
    UserResponseTO toDTOResponseFromEntity(UserEntity user);
}
