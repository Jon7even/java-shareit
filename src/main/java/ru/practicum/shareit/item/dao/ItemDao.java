package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.entity.Item;

import java.util.List;
import java.util.Optional;

public interface ItemDao {
    Optional<Item> createItem(Item item);

    List<Item> getAllItemsByUserId(long idUser);

    Optional<Item> findItemById(long idItem);

    Optional<Item> updateItem(Item item);

    List<Item> getListSearchItem(String text);
}
