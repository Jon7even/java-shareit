package ru.practicum.shareit.item.controller;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;

@Component
public class ItemControllerMapperIml implements ItemControllerMapper {
    private ItemControllerMapperIml() {
    }

    @Override
    public ItemCreateInServiceDTO toItemInServiceFromItemRequestCreateDTO(ItemRequestCreateDTO itemRequest,
                                                                          long idUser) {

        return ItemCreateInServiceDTO.builder()
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(idUser)
                .build();
    }

    @Override
    public ItemResponseDTO toItemResponseDTOFromItem(Item item) {

        return ItemResponseDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }

    @Override
    public ItemUpdateInServiceDTO toItemInServiceFromItemRequestUpdateDTO(ItemRequestUpdateDTO itemRequest,
                                                                          long idUser, long idItem) {
        return ItemUpdateInServiceDTO.builder()
                .id(idItem)
                .name(itemRequest.getName())
                .description(itemRequest.getDescription())
                .available(itemRequest.getAvailable())
                .owner(idUser)
                .build();
    }
}
