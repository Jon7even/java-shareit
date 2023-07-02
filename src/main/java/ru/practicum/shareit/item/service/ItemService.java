package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemResponseDTO createItem(ItemRequestCreateDTO itemRequestCreateDTO, Optional<Long> idUser);

    ItemResponseDTO findItemById(Optional<Long> idUser, Optional<Long> idItem);

    ItemResponseDTO updateItem(Optional<Long> idUser, Optional<Long> idItem, ItemRequestUpdateDTO itemRequestUpdateDTO);

    List<ItemResponseDTO> getAllItemsByUserId(Optional<Long> idUser);

    List<ItemResponseDTO> getListSearchItem(Optional<Long> idUser, Optional<String> searchText);
}
