package ru.practicum.shareit.controllers.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.controllers.setup.GenericMapperTest;
import ru.practicum.shareit.item.dto.ItemResponseTO;
import ru.practicum.shareit.request.dto.ItemRequestCreateTO;
import ru.practicum.shareit.request.dto.ItemRequestRequestListTO;
import ru.practicum.shareit.request.dto.ItemRequestResponseTO;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequestEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest extends GenericMapperTest {

    @Test
    void toEntityFromDTOCreate() {
        initTestVariable(false, false, false);
        ItemRequestCreateTO originalDtoItemRequest = ItemRequestCreateTO.builder().description("test").build();
        ItemRequestEntity resultEntity = ItemRequestMapper.INSTANCE.toEntityFromDTOCreate(
                originalDtoItemRequest, userEntity, currentTime
        );

        assertNotNull(resultEntity);
        assertNull(resultEntity.getId());
        assertEquals(resultEntity.getDescription(), originalDtoItemRequest.getDescription());
        assertEquals(resultEntity.getRequestor().getId(), userEntity.getId());
        assertEquals(resultEntity.getRequestor().getName(), userEntity.getName());
        assertEquals(resultEntity.getRequestor().getEmail(), userEntity.getEmail());
        assertEquals(resultEntity.getCreated(), currentTime);
    }

    @Test
    void toDTOResponseFromEntity() {
        initTestVariable(false, false, true);
        List<ItemResponseTO> items = new ArrayList<>();
        items.add(ItemResponseTO.builder()
                .id(id).name("testName")
                .description("testDescription")
                .available(true)
                .requestId(id)
                .build());
        ItemRequestResponseTO resultDTO = ItemRequestMapper.INSTANCE.toDTOResponseFromEntity(itemRequestEntity, items);

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getId(), itemRequestEntity.getId());
        assertEquals(resultDTO.getDescription(), itemRequestEntity.getDescription());
        assertEquals(resultDTO.getCreated(), currentTime);
        assertEquals(resultDTO.getItems(), items);
    }

    @Test
    void toDTOFromRequestParam() {
        initOptionalVariable();
        ItemRequestRequestListTO resultDTO = ItemRequestMapper.INSTANCE.toDTOFromRequestParam(
                idUserOptional, fromOptional, sizeOptional
        );

        assertNotNull(resultDTO);
        assertEquals(resultDTO.getIdUser(), idUserOptional);
        assertEquals(resultDTO.getFrom(), fromOptional);
        assertEquals(resultDTO.getSize(), sizeOptional);
    }

}
