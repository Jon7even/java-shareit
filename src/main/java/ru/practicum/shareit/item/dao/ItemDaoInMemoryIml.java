package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemUpdateInRepositoryDTO;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Item> getAllItemsByUserId(long idUser) {
        return items.values().stream().filter(item -> item.getOwner().getId() == idUser).collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findItemById(long idItem) {
        return Optional.ofNullable(items.get(idItem));
    }

    @Override
    public Optional<Item> updateItem(ItemUpdateInRepositoryDTO itemUpdateInRepositoryDTO) {
        long idItem = itemUpdateInRepositoryDTO.getId();

        Item updateItem = Item.builder()
                .id(itemUpdateInRepositoryDTO.getId())
                .name(itemUpdateInRepositoryDTO.getName())
                .description(itemUpdateInRepositoryDTO.getDescription())
                .available(itemUpdateInRepositoryDTO.isAvailable())
                .owner(itemUpdateInRepositoryDTO.getOwner())
                .build();

        items.put(idItem, updateItem);

        return Optional.of(items.get(idItem));
    }

    @Override
    public boolean checkIsUserTheOwnerOfItem(long idItem, User user) {
        return items.get(idItem).getOwner().getId() == user.getId();
    }
}