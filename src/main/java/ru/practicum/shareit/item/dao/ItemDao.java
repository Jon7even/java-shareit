package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.entity.Item;

import java.util.Optional;

public interface ItemDao {
    Optional<Item> createItem(Item item);
}
