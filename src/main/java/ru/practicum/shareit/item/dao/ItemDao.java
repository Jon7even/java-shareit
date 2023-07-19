package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.projections.ItemShort;

import java.util.List;

@Component
public interface ItemDao extends JpaRepository<Item, Long> {

    List<Item> findByUserId(Long userId);

    @Query("SELECT new ru.practicum.shareit.item.projections.ItemShort(it.id, it.name, it.description, it.available) " +
            " FROM Item AS it " +
            "WHERE it.available = true " +
            "  AND (LOWER(it.name) LIKE LOWER(CONCAT('%', ?1,'%')) " +
            "       OR LOWER(it.description) LIKE LOWER(CONCAT('%', ?1,'%'))" +
            "      )")
    List<ItemShort> getListSearchItemShort(String text);

}