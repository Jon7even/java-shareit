package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingQueueDTO;
import ru.practicum.shareit.item.dto.CommentResponseDTO;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemResponseBookingAndCommentDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "itemRequestCreateDTO.name", target = "name")
    @Mapping(source = "itemRequestCreateDTO.description", target = "description")
    @Mapping(source = "itemRequestCreateDTO.available", target = "available")
    @Mapping(source = "user", target = "user")
    Item toEntityFromDTOCreate(ItemRequestCreateDTO itemRequestCreateDTO, User user);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    ItemResponseDTO toDTOResponseFromEntity(Item item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "comments", target = "comments")
    ItemResponseBookingAndCommentDTO toDTOResponseWithCommentsFromEntity(Item item, List<CommentResponseDTO> comments);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "last", target = "lastBooking")
    @Mapping(source = "next", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemResponseBookingAndCommentDTO toDTOResponseWithCommentsByOwnerFromEntity(Item item,
                                                                                BookingQueueDTO last,
                                                                                BookingQueueDTO next,
                                                                                List<CommentResponseDTO> comments);
}
