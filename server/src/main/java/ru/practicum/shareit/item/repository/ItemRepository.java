package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.ItemEntity;
import ru.practicum.shareit.item.projections.ItemShort;
import ru.practicum.shareit.request.model.ItemRequestEntity;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    List<ItemEntity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT new ru.practicum.shareit.item.projections.ItemShort(it.id, it.name, it.description, it.available) " +
            " FROM ItemEntity AS it " +
            "WHERE it.available = true " +
            "  AND (LOWER(it.name) LIKE LOWER(CONCAT('%', ?1,'%')) " +
            "       OR LOWER(it.description) LIKE LOWER(CONCAT('%', ?1,'%'))" +
            "      )")
    List<ItemShort> getListSearchItemShort(String text, Pageable pageable);

    @Query("SELECT it " +
            " FROM ItemEntity AS it " +
            " JOIN FETCH it.user " +
            " JOIN FETCH it.request " +
            "WHERE it.request = ?1 " +
            "ORDER BY it.id ")
    List<ItemEntity> findAllItemsByRequest(ItemRequestEntity itemRequestEntity);

}