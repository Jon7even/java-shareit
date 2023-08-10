package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;

import java.util.List;
import java.util.Optional;

public interface ItemRequestService {
    ItemRequestResponseTO createItemRequest(ItemRequestCreateTO requestCreateTO, Optional<Long> idUser);

    ItemRequestResponseTO findItemRequestById(Optional<Long> idUser, Optional<Long> requestId);

    List<ItemRequestResponseTO> getAllItemRequestByIdOwner(Optional<Long> idUser);

    List<ItemRequestResponseTO> getListItemRequestByAnyUser(ItemRequestRequestListTO itemRequestRequestListTO);
}
