package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemResponseTO;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.model.ItemRequestEntity;
import ru.practicum.shareit.user.model.UserEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequestMapper INSTANCE = Mappers.getMapper(ItemRequestMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "itemRequestCreateTO.description", target = "description")
    @Mapping(source = "user", target = "requestor")
    @Mapping(source = "created", target = "created")
    ItemRequestEntity toEntityFromDTOCreate(ItemRequestCreateTO itemRequestCreateTO,
                                            UserEntity user,
                                            LocalDateTime created);


    @Mapping(source = "itemRequestEntity.id", target = "id")
    @Mapping(source = "itemRequestEntity.description", target = "description")
    @Mapping(source = "items", target = "items")
    ItemRequestResponseTO toDTOResponseFromEntity(ItemRequestEntity itemRequestEntity, List<ItemResponseTO> items);

    @Mapping(source = "idUser", target = "idUser")
    @Mapping(source = "from", target = "from")
    @Mapping(source = "size", target = "size")
    ItemRequestRequestListTO toDTOFromRequestParam(Long idUser,
                                                   Optional<Integer> from,
                                                   Optional<Integer> size);
}
