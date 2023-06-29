package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;

import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ItemDaoInMemoryIml implements ItemDao {
    private final Map<Long, Item> items;
    private static long generatorId;

    @Override
    public Optional<Item> createItem(Item item) {
        long idItem = ++generatorId;
        item.setId(idItem);
        items.put(idItem, item);

        return Optional.of(items.get(idItem));
    }
}
