package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.projections.ItemShort;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemResponseDTO createItem(ItemRequestCreateDTO itemRequestCreateDTO, Optional<Long> idUser);

    ItemResponseBookingAndCommentDTO findItemById(Optional<Long> idUser, Optional<Long> idItem);

    ItemResponseDTO updateItem(Optional<Long> idUser, Optional<Long> idItem, ItemRequestUpdateDTO itemRequestUpdateDTO);

    List<ItemResponseBookingAndCommentDTO> getAllItemsByUserId(Optional<Long> idUser);

    List<ItemShort> getListSearchItem(Optional<Long> idUser, Optional<String> searchText);

    void deleteItemById(Optional<Long> idUser, Optional<Long> idItem);

    CommentResponseDTO createComment(Optional<Long> idUser, Optional<Long> idItem, CommentRequestCreateDTO comment);
}
