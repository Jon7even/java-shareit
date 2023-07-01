package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateInServiceDTO;
import ru.practicum.shareit.item.dto.ItemUpdateInServiceDTO;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemCreateInServiceDTO itemCreateInServiceDTO);

    Item findItemById(long idUser, long idItem);

    List<Item> getAllItemsByUserId(long idUser);

    Item updateItem(ItemUpdateInServiceDTO itemUpdateInServiceDTO);

    List<Item> getListSearchItem(long idUser, String text);
}
