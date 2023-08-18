package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("SELECT com " +
            " FROM CommentEntity AS com " +
            " JOIN FETCH com.item AS it " +
            " JOIN FETCH com.user " +
            "WHERE it = ?1 " +
            "ORDER BY com.created " +
            " DESC")
    List<CommentEntity> findAllCommentsByItem(ItemEntity item);
}
