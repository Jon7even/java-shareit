package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.CommentRequestCreateDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "comment.text", target = "text")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "created", target = "created")
    Comment toEntityFromDTOCreate(CommentRequestCreateDTO comment, Item item, User user, LocalDateTime created);

    @Mapping(source = "comment.id", target = "id")
    @Mapping(source = "comment.text", target = "text")
    @Mapping(source = "comment.user.name", target = "authorName")
    @Mapping(source = "comment.created", target = "created")
    CommentResponseDTO toDTOResponseFromEntity(Comment comment);
}
