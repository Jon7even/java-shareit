package ru.practicum.shareit.controllers.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.controllers.setup.GenericMapperTest;
import ru.practicum.shareit.item.dto.CommentCreateTO;
import ru.practicum.shareit.item.dto.CommentResponseTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.CommentEntity;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest extends GenericMapperTest {

    @Test
    void toEntityFromDTOCreate() {
        initTestVariable(true, false, false);
        CommentCreateTO commentOriginal = CommentCreateTO.builder().text("testCommentDTO").build();
        CommentEntity resultEntity = CommentMapper.INSTANCE.toEntityFromDTOCreate(
                commentOriginal, itemEntity, userEntity, currentTime
        );

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals(resultEntity.getText(), commentOriginal.getText());
        assertEquals(resultEntity.getItem(), itemEntity);
        assertEquals(resultEntity.getUser(), userEntity);
        assertEquals(resultEntity.getCreated(), currentTime);
    }

    @Test
    void toDTOResponseFromEntity() {
        initTestVariable(true, false, false);
        CommentResponseTO resultDTO = CommentMapper.INSTANCE.toDTOResponseFromEntity(commentEntity);

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), commentEntity.getId());
        assertEquals(resultDTO.getText(), commentEntity.getText());
        assertEquals(resultDTO.getAuthorName(), commentEntity.getUser().getName());
        assertEquals(resultDTO.getCreated(), commentEntity.getCreated());
    }

}
