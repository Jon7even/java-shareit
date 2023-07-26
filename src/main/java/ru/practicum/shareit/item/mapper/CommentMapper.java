package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentCreateTO;
import ru.practicum.shareit.item.dto.CommentResponseTO;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "comment.text", target = "text")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "created", target = "created")
    CommentEntity toEntityFromDTOCreate(CommentCreateTO comment, ItemEntity item, UserEntity user, LocalDateTime created);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "comment.text", target = "text")
    @Mapping(source = "comment.user.name", target = "authorName")
    @Mapping(source = "comment.created", target = "created")
    CommentResponseTO toDTOResponseFromEntity(CommentEntity comment);
}
