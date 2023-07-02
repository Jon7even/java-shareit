package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

public final class MapperItemDTO {

    private MapperItemDTO() {
    }

    public static ItemCreateInServiceDTO toItemInServiceFromItemRequestCreateDTO(ItemRequestCreateDTO itemRequest,
                                                                          long idUser) {

        return ItemCreateInServiceDTO.builder()
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(idUser)
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

    public static ItemUpdateInServiceDTO toItemInServiceFromItemRequestUpdateDTO(ItemRequestUpdateDTO itemRequest,
                                                                          long idUser, long idItem) {
        return ItemUpdateInServiceDTO.builder()
                .id(idItem)
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(idUser)
                .build();
    }

    public static Item toItemFromItemInRepositoryUpdateDTO(ItemUpdateInRepositoryDTO itemUpdateInRepositoryDTO) {

        return Item.builder()
                .id(itemUpdateInRepositoryDTO.getId())
                .name(itemUpdateInRepositoryDTO.getName())
                .description(itemUpdateInRepositoryDTO.getDescription())
                .available(itemUpdateInRepositoryDTO.getAvailable())
                .owner(itemUpdateInRepositoryDTO.getOwner())
                .build();
    }

    public static Item toItemFromItemCreateInServiceDTO(ItemCreateInServiceDTO itemCreateInServiceDTO, User user) {
        return Item.builder()
                .name(itemCreateInServiceDTO.getName())
                .description(itemCreateInServiceDTO.getDescription())
                .available(itemCreateInServiceDTO.getAvailable())
                .owner(user)
                .build();
    }

}
