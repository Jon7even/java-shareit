package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.entity.Item;

public interface ItemService {
    Item createItem(ItemCreateInServiceDTO itemCreateInServiceDTO);
}
