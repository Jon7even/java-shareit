package ru.practicum.shareit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(source = "itemRequestCreateDTO.name", target = "name")
    @Mapping(source = "itemRequestCreateDTO.description", target = "description")
    @Mapping(source = "itemRequestCreateDTO.available", target = "available")
    @Mapping(source = "user", target = "owner")
    Item toEntityFromDTOCreate(ItemRequestCreateDTO itemRequestCreateDTO, User user);

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.name", target = "name")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.available", target = "available")
    ItemResponseDTO toDTOResponseFromEntity(Item item);
}
