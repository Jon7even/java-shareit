package ru.practicum.shareit.controllers.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingQueueTO;
import ru.practicum.shareit.controllers.setup.GenericMapperTest;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.ItemEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest extends GenericMapperTest {

    @Test
    void toEntityFromDTOCreate() {
        initTestVariable(false, false, false);
        ItemCreateTO originalDTO = ItemCreateTO.builder()
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .requestId(id)
                .build();
        ItemEntity resultEntity = ItemMapper.INSTANCE.toEntityFromDTOCreate(originalDTO, userEntity);

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertNull(resultEntity.getRequest());
        assertEquals(resultEntity.getName(), originalDTO.getName());
        assertEquals(resultEntity.getDescription(), originalDTO.getDescription());
        assertEquals(resultEntity.isAvailable(), originalDTO.getAvailable());
        assertEquals(resultEntity.getUser().getId(), userEntity.getId());
        assertEquals(resultEntity.getUser().getName(), userEntity.getName());
        assertEquals(resultEntity.getUser().getEmail(), userEntity.getEmail());
    }

    @Test
    void toEntityFromDTOCreateWithRequest() {
        initTestVariable(false, false, true);
        ItemCreateTO originalDTO = ItemCreateTO.builder()
                .name("TestName")
                .description("TestDescription")
                .available(true)
                .requestId(id)
                .build();
        ItemEntity resultEntity = ItemMapper.INSTANCE.toEntityFromDTOCreateWithRequest(
                originalDTO, userEntity, itemRequestEntity
        );

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals(resultEntity.getRequest(), itemRequestEntity);
        assertEquals(resultEntity.getName(), originalDTO.getName());
        assertEquals(resultEntity.getDescription(), originalDTO.getDescription());
        assertEquals(resultEntity.isAvailable(), originalDTO.getAvailable());
        assertEquals(resultEntity.getUser().getId(), userEntity.getId());
        assertEquals(resultEntity.getUser().getName(), userEntity.getName());
        assertEquals(resultEntity.getUser().getEmail(), userEntity.getEmail());
    }

    @Test
    void toDTOResponseFromEntity() {
        initTestVariable(true, false, true);
        ItemResponseTO resultDTO = ItemMapper.INSTANCE.toDTOResponseFromEntity(itemEntity);

        assertNull(resultDTO.getRequestId());
        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), itemEntity.getId());
        assertEquals(resultDTO.getName(), itemEntity.getName());
        assertEquals(resultDTO.getDescription(), itemEntity.getDescription());
        assertEquals(resultDTO.getAvailable(), itemEntity.isAvailable());

        itemEntity.setRequest(itemRequestEntity);
        ItemResponseTO resultWithRequestDTO = ItemMapper.INSTANCE.toDTOResponseFromEntity(itemEntity);

        assertNotNull(resultWithRequestDTO);
        assertEquals(resultWithRequestDTO.getId(), itemEntity.getId());
        assertEquals(resultWithRequestDTO.getName(), itemEntity.getName());
        assertEquals(resultWithRequestDTO.getDescription(), itemEntity.getDescription());
        assertEquals(resultWithRequestDTO.getAvailable(), itemEntity.isAvailable());
        assertEquals(resultWithRequestDTO.getRequestId(), itemEntity.getRequest().getId());
    }

    @Test
    void toDTOResponseWithCommentsFromEntity() {
        initTestVariable(true, false, false);
        List<CommentResponseTO> commentsOriginal = new ArrayList<>();
        commentsOriginal.add(commentDTO);

        ItemResponseBookingAndCommentTO resultDTO = ItemMapper.INSTANCE.toDTOResponseWithCommentsFromEntity(
                itemEntity, commentsOriginal
        );

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), itemEntity.getId());
        assertEquals(resultDTO.getName(), itemEntity.getName());
        assertEquals(resultDTO.getDescription(), itemEntity.getDescription());
        assertEquals(resultDTO.getAvailable(), itemEntity.isAvailable());
        assertEquals(resultDTO.getComments(), commentsOriginal);
        assertNull(resultDTO.getLastBooking());
        assertNull(resultDTO.getNextBooking());
    }

    @Test
    void toDTOResponseWithCommentsByOwnerFromEntity() {
        initTestVariable(true, false, false);
        List<CommentResponseTO> commentsOriginal = new ArrayList<>();
        commentsOriginal.add(commentDTO);
        BookingQueueTO lastOriginal = BookingQueueTO.builder().id(1L).bookerId(2L).build();
        BookingQueueTO nextOriginal = BookingQueueTO.builder().id(3L).bookerId(4L).build();

        ItemResponseBookingAndCommentTO resultDTO = ItemMapper.INSTANCE.toDTOResponseWithCommentsByOwnerFromEntity(
                itemEntity, lastOriginal, nextOriginal, commentsOriginal
        );

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), itemEntity.getId());
        assertEquals(resultDTO.getName(), itemEntity.getName());
        assertEquals(resultDTO.getDescription(), itemEntity.getDescription());
        assertEquals(resultDTO.getAvailable(), itemEntity.isAvailable());
        assertEquals(resultDTO.getComments(), commentsOriginal);
        assertEquals(resultDTO.getLastBooking(), lastOriginal);
        assertEquals(resultDTO.getNextBooking(), nextOriginal);
    }

    @Test
    void toDTOFromRequestParam() {
        initOptionalVariable();
        ItemRequestListTO resultDTO = ItemMapper.INSTANCE.toDTOFromRequestParam(
                idUserOptional, fromOptional, sizeOptional, textOptional
        );

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getIdUser(), idUserOptional);
        assertEquals(resultDTO.getFrom(), fromOptional);
        assertEquals(resultDTO.getSize(), sizeOptional);
        assertEquals(resultDTO.getText(), textOptional);
    }

    @Test
    void toDTOFromRequestParamWithoutText() {
        initOptionalVariable();
        ItemRequestListTO resultDTO = ItemMapper.INSTANCE.toDTOFromRequestParamWithoutText(
                idUserOptional, fromOptional, sizeOptional
        );

        assertNotNull(resultDTO);
        assertNull(resultDTO.getText());
        assertEquals(resultDTO.getIdUser(), idUserOptional);
        assertEquals(resultDTO.getFrom(), fromOptional);
        assertEquals(resultDTO.getSize(), sizeOptional);
    }

}
