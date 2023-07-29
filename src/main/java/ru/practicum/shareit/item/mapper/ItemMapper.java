package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "itemRequestCreateDTO.name", target = "name")
    @Mapping(source = "itemRequestCreateDTO.description", target = "description")
    @Mapping(source = "itemRequestCreateDTO.available", target = "available")
    @Mapping(source = "user", target = "user")
    ItemEntity toEntityFromDTOCreate(ItemCreateTO itemRequestCreateDTO, UserEntity user);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    ItemResponseTO toDTOResponseFromEntity(ItemEntity item);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "comments", target = "comments")
    ItemResponseBookingAndCommentTO toDTOResponseWithCommentsFromEntity(ItemEntity item,
                                                                        List<CommentResponseTO> comments);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    @Mapping(source = "last", target = "lastBooking")
    @Mapping(source = "next", target = "nextBooking")
    @Mapping(source = "comments", target = "comments")
    ItemResponseBookingAndCommentTO toDTOResponseWithCommentsByOwnerFromEntity(ItemEntity item,
                                                                               BookingQueueTO last,
                                                                               BookingQueueTO next,
                                                                               List<CommentResponseTO> comments);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "size", target = "size")
    @Mapping(source = "text", target = "text")
    ItemRequestListTO toDTOFromRequestParam(Optional<Long> idUser,
                                            Optional<Integer> from,
                                            Optional<Integer> size,
                                            Optional<String> text);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "size", target = "size")
    @Mapping(target = "text", ignore = true)
    ItemRequestListTO toDTOFromRequestParamWithoutText(Optional<Long> idUser,
                                                       Optional<Integer> from,
                                                       Optional<Integer> size);

}
