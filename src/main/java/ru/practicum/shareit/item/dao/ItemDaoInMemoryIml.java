package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.utils.IdGeneratorItem;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDaoInMemoryIml implements ItemDao {
    private final Map<Long, Item> items;
    private final IdGeneratorItem idGenerator;

    @Override
    public Optional<Item> createItem(Item item) {
        long idItem = idGenerator.generateId();
        item.setId(idItem);
        items.put(idItem, item);

        return Optional.of(items.get(idItem));
    }

    @Override
    public List<Item> getAllItemsByUserId(long idUser) {
        return items.values().stream().filter(item -> item.getOwner().getId() == idUser).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(long idItem) {
        return Optional.ofNullable(items.get(idItem));
    }

    @Override
    public Optional<Item> updateItem(Item item) {
        long idItem = item.getId();

        items.put(idItem, item);

        return Optional.of(items.get(idItem));
    }

    @Override
    public List<Item> getListSearchItem(String text) {
        String textLowCase = text.toLowerCase();

        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(i -> i.getName().toLowerCase().contains(textLowCase)
                        || i.getDescription().toLowerCase().contains(textLowCase))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteItemById(long idItem) {
        items.remove(idItem);

        return findItemById(idItem).isEmpty();
    }

}
