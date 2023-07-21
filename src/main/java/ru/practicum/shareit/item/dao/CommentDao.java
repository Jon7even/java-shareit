package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

@Component
public interface CommentDao extends JpaRepository<Comment, Long> {
    @Query("SELECT com " +
            " FROM Comment AS com " +
            " JOIN FETCH com.item AS it " +
            " JOIN FETCH com.user " +
            "WHERE it = ?1 " +
            "ORDER BY com.created " +
            " DESC")
    List<Comment> findAllCommentsByItem(Item item);
}
