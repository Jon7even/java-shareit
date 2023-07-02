package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

public final class MapperItemDTO {

    private MapperItemDTO() {
    }

    public static Item toItemFromItemRequestCreateDTO(ItemRequestCreateDTO itemRequestCreateDTO,
                                                                          User user) {

        return Item.builder()
                .name(itemRequestCreateDTO.getName())
                .description(itemRequestCreateDTO.getDescription())
                .available(itemRequestCreateDTO.getAvailable())
                .owner(user)
                .build();
    }

    public static ItemResponseDTO toItemResponseDTOFromItem(Item item) {

        return ItemResponseDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

}
