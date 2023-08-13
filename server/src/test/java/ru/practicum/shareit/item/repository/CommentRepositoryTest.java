package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.model.CommentEntity;
import ru.practicum.shareit.setup.GenericRepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CommentRepositoryTest extends GenericRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ItemRepository itemRepository;

    private CommentEntity commentEntityInDb;

    @BeforeEach
    void setUp_Two() {
        initTestVariable(true, false, true);
        itemRepository.save(itemEntity);
        commentEntityInDb = commentRepository.save(commentEntity);
    }

    @Test
    void saveComment() {
        assertNotNull(commentEntityInDb);
        assertEquals(commentEntityInDb.getId(), commentEntity.getId());
        assertEquals(commentEntityInDb.getText(), commentEntity.getText());
        assertEquals(commentEntityInDb.getItem(), commentEntity.getItem());
        assertEquals(commentEntityInDb.getUser(), commentEntity.getUser());
        assertEquals(commentEntityInDb.getCreated(), commentEntity.getCreated());
    }

    @Test
    void findComment() {
        Optional<CommentEntity> result = commentRepository.findById(commentEntityInDb.getId());

        assertNotNull(result);
        assertEquals(result.get().getId(), commentEntity.getId());
        assertEquals(result.get().getText(), commentEntity.getText());
        assertEquals(result.get().getItem(), commentEntity.getItem());
        assertEquals(result.get().getUser(), commentEntity.getUser());
        assertEquals(result.get().getCreated(), commentEntity.getCreated());
    }

    @Test
    void findAllCommentsByItem() {
        List<CommentEntity> result = commentRepository.findAllCommentsByItem(itemEntity);

        assertNotNull(result);
        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), commentEntity.getId());
        assertEquals(result.get(0).getText(), commentEntity.getText());
        assertEquals(result.get(0).getItem(), commentEntity.getItem());
        assertEquals(result.get(0).getUser(), commentEntity.getUser());
        assertEquals(result.get(0).getCreated(), commentEntity.getCreated());
    }

}
