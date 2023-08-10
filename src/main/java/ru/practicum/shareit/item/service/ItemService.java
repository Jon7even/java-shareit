package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.projections.ItemShort;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemResponseTO createItem(ItemCreateTO itemCreateTO, Optional<Long> idUser);

    ItemResponseBookingAndCommentTO findItemById(Optional<Long> idUser, Optional<Long> idItem);

    ItemResponseTO updateItem(Optional<Long> idUser, Optional<Long> idItem, ItemUpdateTO itemUpdateTO);

    List<ItemResponseBookingAndCommentTO> getAllItemsByUserId(ItemRequestListTO itemRequestListTO);

    List<ItemShort> getListSearchItem(ItemRequestListTO itemRequestListTO);

    void deleteItemById(Optional<Long> idUser, Optional<Long> idItem);

    CommentResponseTO createComment(Optional<Long> idUser, Optional<Long> idItem, CommentCreateTO comment);
}
