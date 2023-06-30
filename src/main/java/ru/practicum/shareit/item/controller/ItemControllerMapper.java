package ru.practicum.shareit.item.controller;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Item;

public interface ItemControllerMapper {
    ItemCreateInServiceDTO toItemInServiceFromItemRequestCreateDTO(ItemRequestCreateDTO itemRequest, long idUser);

    ItemResponseDTO toItemResponseDTOFromItem(Item item);

    ItemUpdateInServiceDTO toItemInServiceFromItemRequestUpdateDTO(ItemRequestUpdateDTO itemRequest,
                                                                   long idUser, long idItem);
}
