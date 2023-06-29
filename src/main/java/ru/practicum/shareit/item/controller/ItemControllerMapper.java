package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.dto.ItemRequestCreateDTO;
import ru.practicum.shareit.item.dto.ItemResponseDTO;
import ru.practicum.shareit.item.entity.Item;

public interface ItemControllerMapper {
    ItemCreateInServiceDTO toItemInServiceFromItemRequestDTO(ItemRequestCreateDTO itemRequestCreateDTO, long idUser);

    ItemResponseDTO toItemResponseDTOFromItem(Item item);
}
