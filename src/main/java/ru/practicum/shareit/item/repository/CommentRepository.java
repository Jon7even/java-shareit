package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT com " +
            " FROM Comment AS com " +
            " JOIN FETCH com.item AS it " +
            " JOIN FETCH com.user " +
            "WHERE it = ?1 " +
            "ORDER BY com.created " +
            " DESC")
    List<Comment> findAllCommentsByItem(Item item);
}
