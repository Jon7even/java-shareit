package ru.practicum.shareit.item.controller;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;

@Component
public class ItemControllerMapperIml implements ItemControllerMapper {
    private ItemControllerMapperIml() {
    }

    @Override
    public ItemCreateInServiceDTO toItemInServiceFromItemRequestDTO(ItemRequestCreateDTO itemRequestCreateDTO,
                                                                    long idUser) {
        return ItemCreateInServiceDTO.builder()
                .name(itemRequestCreateDTO.getName())
                .description(itemRequestCreateDTO.getDescription())
                .available(itemRequestCreateDTO.getAvailable())
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
}
